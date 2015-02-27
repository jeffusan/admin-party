package repositories

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait SubCategoryRepository {

  def findAll: JsArray

  def findById(id: Int): JsValue

  def remove(id: Int)

  def add(name: String, category_id: Int): JsValue

  def update(id: Int, name: String, category_id: Int): JsValue
}

object AnormSubCategoryRepository extends SubCategoryRepository with JSONTranslation {

  def update(id: Int, name: String, category_id: Int): JsValue = {
    DB.withConnection{ implicit c =>
      SQL(
        """
        with updated as (
          update subcategory
          set name={name}, category_id={category_id}
          where id={id} returning id, name, category_id
        )
        select
          json_build_object('id', (select id from updated),
          'name', (select name from updated),
          'category', json_build_object(
            'id', (select category_id from updated),
            'name', c.name))
        from subcategory s, category c
        where s.id in (select id from updated)
        and (select category_id from updated) = c.id;
        """
      ).on(
        'id -> id,
        'name -> name,
        'category_id -> category_id
      ).as(simple_build.single)
    }
  }


  def add(name: String, categoryId: Int): JsValue = {
    DB.withConnection { implicit c =>
      SQL(
        """
        with data( id, name, category_id) as (
          insert into subcategory (id, name, category_id)
          values (DEFAULT, {name}, {category_id})
          returning id, name, category_id
        )
        select json_build_object(
          'id', data.id,
          'name', data.name,
          'category', json_build_object('id', data.category_id, 'name', category.name))
            from data, category
          where category.id = data.category_id;
         """
      ).on('name -> name, 'category_id -> categoryId).as(simple_build.single)
    }
  }


  def remove(id: Int) {
    DB.withConnection { implicit c =>
      SQL("""
      delete from subcategory where id={id};
      """).on('id -> id).execute

    }
  }

  def findById(id: Int): JsValue = {
    DB.withConnection { implicit c =>
      SQL("""
      with data as (
        select
          s.id,
          s.name,
          json_build_object(
            'id', c.id,
            'name', c.name
          ) as category
          from category c, subcategory s
          where s.category_id = c.id
          and s.id = {id}
          order by s.name
      )
      select row_to_json(data) from data;
      """).on('id -> id).as(simple.single)
    }
  }

  def findAll: JsArray = {
    DB.withConnection { implicit c =>
      try {
        SQL("""
        with data as (
          select
           s.id,
           s.name,
           json_build_object(
             'id', c.id,
             'name', c.name
           ) as category
           from category c, subcategory s
           where s.category_id = c.id
           order by s.name
        )
        select array_to_json(array_agg(data)) from data;
        """).as(array.single)
      } catch {
        case nse: NoSuchElementException =>
          Json.toJson(Seq("")).asInstanceOf[JsArray]
      }
    }
  }
}

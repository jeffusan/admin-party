package repositories

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait CategoryRepository {

  def findAll: JsArray

  def findById(id: Int): JsValue

  def remove(id: Int)

  def add(name: String): JsValue

  def update(id: Int, name: String): JsValue

}

object AnormCategoryRepository extends CategoryRepository with JSONTranslation {

  def update(id: Int, name: String): JsValue = {
    DB.withConnection{ implicit c =>
      SQL(
        """
        with updated as (
          update category
          set name={name}
          where id={id} returning id, name
        )
        select row_to_json(updated) from updated;
        """
      ).on(
        'id -> id,
        'name -> name
      ).as(simple_build.single)
    }
  }


  def add(name: String): JsValue = {
    DB.withConnection { implicit c =>
      SQL(
        """
        with data( id, name) as (
          insert into category (id, name)
          values (DEFAULT, {name})
          returning id, name
        )
        select row_to_json(data) from data;
         """
      ).on('name -> name).as(simple.single)
    }
  }


  def remove(id: Int) {
    DB.withConnection { implicit c =>
      SQL("""
      delete from category where id={id};
      """).on('id -> id).execute

    }
  }

  def findById(id: Int): JsValue = {
    DB.withConnection { implicit c =>
      SQL("""
      select row_to_json(row)
      from (
        select id, name from category where id={id}
      ) row;
      """).on('id -> id).as(simple.single)
    }
  }

  def findAll: JsArray = {
    DB.withConnection { implicit c =>
      try {
        SQL("""
        select array_to_json(array_agg(category)) from category;
        """).as(array.single)
      } catch {
        case nse: NoSuchElementException =>
          Json.toJson(Seq("")).asInstanceOf[JsArray]
      }
    }
  }

}

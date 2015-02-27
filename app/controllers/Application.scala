package controllers

import play.api._
import play.api.mvc._
import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import repositories.{AnormCategoryRepository, AnormSubCategoryRepository}

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getCategories = Action {
    Ok(AnormCategoryRepository.findAll)
  }

  def getSubCategories = Action {
    Ok(AnormSubCategoryRepository.findAll)
  }

  def getCategory(id: Int) = Action {
    Ok(AnormCategoryRepository.findById(id))
  }

  def getSubCategory(id: Int) = Action {
    Ok(AnormSubCategoryRepository.findById(id))
  }

  case class NewCategoryData(name: String)
  val createForm = Form(
    mapping(
      "name" -> nonEmptyText
    )(NewCategoryData.apply)(NewCategoryData.unapply)
  )
  def createCategory = Action(parse.json) { implicit request =>
    createForm.bindFromRequest().fold(
      formWithErrors => BadRequest(Json.obj("error" -> "だめです")),
      categoryData => {
        Ok(AnormCategoryRepository.add(categoryData.name))
      }
    )
  }

  case class NewSubCategoryData(name: String, category_id: Int)
  val createSubForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "category_id" -> number
    )(NewSubCategoryData.apply)(NewSubCategoryData.unapply)
  )

  def createSubCategory = Action(parse.json) { implicit request =>
    createSubForm.bindFromRequest.fold(
      formWithErrors => BadRequest(Json.obj("error" -> "だめです")),
      subCategoryData => {
        Ok(AnormSubCategoryRepository.add(
          subCategoryData.name,
          subCategoryData.category_id))
      }
    )
  }

  def removeCategory(id: Int) = Action {
    AnormCategoryRepository.remove(id)
    Ok(Json.obj("message" ->"ok"))
  }

  def removeSubCategory(id: Int) = Action {
    AnormSubCategoryRepository.remove(id)
    Ok(Json.obj("message" ->"ok"))
  }

  case class UpdateCategoryData(id: Int, name: String)
  val updateForm = Form(
    mapping(
      "id" -> number,
      "name" -> nonEmptyText
    )(UpdateCategoryData.apply)(UpdateCategoryData.unapply)
  )

  def updateCategory = Action(parse.json) { implicit request =>
    updateForm.bindFromRequest().fold(
      formWithErrors => BadRequest(Json.obj("error" -> "だめです")),
      categoryData => {
        Ok(AnormCategoryRepository.update(categoryData.id, categoryData.name))
      }
    )
  }

  case class UpdateSubCategoryData(id: Int, name: String, category_id: Int)
  val updateSubForm = Form(
    mapping(
      "it" -> number,
      "name" -> nonEmptyText,
      "category_id" -> number
    )(UpdateSubCategoryData.apply)(UpdateSubCategoryData.unapply)
  )

  def updateSubCategory = Action(parse.json) { implicit request =>
    updateSubForm.bindFromRequest().fold(
      formWithErrors => BadRequest(Json.obj("error" -> "だめです")),
      subCategoryData => {
        Ok(AnormSubCategoryRepository.update(
          subCategoryData.id,
          subCategoryData.name,
          subCategoryData.category_id))
      }
    )
  }

}

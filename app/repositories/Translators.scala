package repositories

import anorm._
import anorm.SqlParser._
import play.api.libs.json._
import org.postgresql.util.PGobject
import play.api.db.DB
import play.api.Play.current
import play.api.Logger
import play.api.libs.functional.syntax._

trait JSONTranslation {

  implicit def rowToJsValue: Column[JsValue] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case pgo: PGobject => Right({
        pgo.getType match {
          case "json" => Json.parse(pgo.getValue)
          case _ => JsNull
        }
      })
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" +
      value.asInstanceOf[AnyRef].getClass + " to JsValue for column " + qualified))
    }
  }

  implicit def rowToJsArray: Column[JsArray] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case pgo: PGobject => Right({
        pgo.getType match {
          case "json" => Json.parse(pgo.getValue).asInstanceOf[JsArray]
          case _ => new JsArray()
        }
      })
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" +
      value.asInstanceOf[AnyRef].getClass + " to JsValue for column " + qualified))
    }
  }

  val simple = {
    get[JsValue]("row_to_json") map {
      case row_to_json =>
        row_to_json.asInstanceOf[JsValue]
    }
  }

  val simple_build = {
    get[JsValue]("json_build_object") map {
      case json_build_object =>
        json_build_object.asInstanceOf[JsValue]
    }
  }

  val array = {
    get[JsArray]("array_to_json") map {
      case array_to_json =>
        array_to_json.asInstanceOf[JsArray]
    }
  }

}

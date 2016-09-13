package codes.bytes.quaich.demo.http

import codes.bytes.quaich.api.http.model.LambdaHttpRequest
import codes.bytes.quaich.api.http.{HTTPApp, LambdaContext}
import com.amazonaws.HttpMethod

object DemoHTTPServer extends HTTPApp {

  @route("/")
  def index(req: LambdaHttpRequest, ctx: LambdaContext): String = {
    """{ "hello" : "world" }"""
  }

  @route("/users/{username}")
  def stateOfCity(req: LambdaHttpRequest, ctx: LambdaContext,
    username: String): String = {
    val user = db.getUser(username) match {
      case Some(userData) => userData.toJson
      case None =>
        ctx.log(s"ERROR: User '$username' not found")
        s""" { "error": "No such user" } """
    }
  }

  @route("/users", methods=Vector(HttpMethod.POST))
  def createUser(req: LambdaHttpRequest, ctx: LambdaContext): String = {
    // Lambda will handle exceptions somewhat nicely, 500ing and logging
    val userData = req.body.extract[User]
    db.createUser(userData)
    s""" { "success": 1 } """
  }

}

// vim: set ts=2 sw=2 sts=2 et:
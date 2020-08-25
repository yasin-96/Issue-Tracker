package de.thm.webservices.issuetracker.gatling

import scala.concurrent.duration
import io.gatling.core.Predef
import io.gatling.http.Predef
import io.gatling.jdbc.Predef

class LoginTest extends Simulation {

  val sessionHeaders = Map("Authorization" -> "Bearer ${authToken}",
    "Content-Type" -> "application/json")

  val httpProtocol = http
    .baseURL("http://xx.xx.xx.xx:3000/%22")

  val scn = scenario("login_test")
    // LogIn
    .exec(http("login")
      .post("/api/login")
      .formParam("organization_id", "4666")
      .formParam("email", "jdoe@example.com")
      .formParam("password", "put_password_here")
      .check(jsonPath("$..token").exists.saveAs("authToken"))
    )
    .exec(http("get_alerts")
      .get("/api/alerts")
      .headers(sessionHeaders)
    )
    .exec(http("create_widget")
      .post("/api/widgets")
      .headers(sessionHeaders)
      .body(StringBody("""{"description":"This is just a sample description.","name":"Junk"}"""))
      .check(jsonPath("$..id").exists.saveAs("newWidgetId"))
    )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
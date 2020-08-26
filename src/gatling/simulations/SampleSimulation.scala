
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class SampleSimulation extends Simulation {

  val sessionHeaders = Map("Authorization" -> "Bearer ${authToken}",
    "Content-Type" -> "application/json")

  val httpProtocol = http.baseUrl("http://localhost:8080/")

  val scn = scenario("login_test")
    // LogIn
    .exec(http("login")
      .get("auth/login?username=alex&password=alex")
      .check(jsonPath("$..token").exists.saveAs("authToken"))
    )
  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}


import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

/*
class SampleSimulation extends Simulation {

  object Search {
    val search = exec(
      http("Login")
        .post("/auth/login")
        .formParam("username", "alex")
        .formParam("password", "alex")
        .check(jsonPath("$..token").exists.saveAs("authToken"))
    ).pause(1)
  }

  val httpProtocol = http
     http.baseUrl("http://localhost:8080/")
    .acceptHeader("application/json")

  val users = scenario("User Login").exec(Search.search)

  setUp(users.inject(rampUsers(1) during (1 seconds))).protocols(httpProtocol)
}
*/
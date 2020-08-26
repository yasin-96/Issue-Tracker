import io.gatling.core.Predef._
import io.gatling.http.Predef._

/*
class LoginTest extends Simulation {

  val sessionHeaders = Map("Authorization" -> "Bearer ${authToken}",
    "Content-Type" -> "application/json")

  val httpProtocol = http.baseUrl("http://localhost:8080/")

  val scn = scenario("login_test")
    // LogIn
    .exec(http("login")
      .post("/auth/login")
      .formParam("username", "alex")
      .formParam("password", "alex")
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
}*/
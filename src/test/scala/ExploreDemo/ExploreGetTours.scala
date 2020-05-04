
package ExploreDemo

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ExploreGetTours extends Simulation {

  val sessionHeaders = Map("Authorization" -> "Bearer ${authToken}",
    "Content-Type" -> "application/json")

  val httpConf = http.baseUrl("https://exploresolutionapi.azurewebsites.net/")
  val scn = scenario("Basic Simulation with Gatling for Explore-Solution")
    .exec(sign_in())
    .pause(5)
    .exec(http("getAllTours")
      .get("/api/tours/getAll")
      .headers(sessionHeaders)
      .check(status.is(200)))
    .pause(5)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

  def sign_in() = {
    exec(
      http("sign in ExploreSolution")
        .post("/api/Authentication/request")
        .header("Content-Type", "application/json")
        .body(StringBody("{\"username\":\"user\", \"password\":\"pass\"}"))
        .check(status.is(200))
        .check(bodyString.saveAs("authToken"))
    )
  }

}


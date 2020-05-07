
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
      .check(status.is(200))
      //.check(bodyString.isNotNull())
      .check(responseTimeInMillis.lte(25000)))
    .pause(5)

  val scenarioWithLoop = scenario("Basic Simulation with Loop")
      .repeat(20) {
        exec(sign_in())
          .pause(5)
          .exec(http("getAllTours")
            .get("/api/tours/getAll")
            .headers(sessionHeaders)
            .check(status.is(200)))
          .pause(5)
      }

  //setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)

  //setUp(scn.inject(rampUsers(10) during(10)) ).protocols(httpConf)

  //setUp((scenarioWithLoop.inject(rampUsersPerSec(10) to 20 during (30)))).protocols(httpConf)
  setUp((scenarioWithLoop.inject(constantUsersPerSec(5) during(5)))).assertions(forAll.failedRequests.percent.lte(2)) .protocols(httpConf)
/*
  setUp(
    scn.inject(
      nothingFor(4), // 1
      atOnceUsers(10), // 2
      rampUsers(10) during (5), // 3
      constantUsersPerSec(20) during (15 ), // 4
      constantUsersPerSec(20) during (15 ) randomized, // 5
      rampUsersPerSec(10) to 20 during (10 ), // 6
      rampUsersPerSec(10) to 20 during (10 ) randomized, // 7
      heavisideUsers(1000) during (20 ) // 8
    ).protocols(httpConf)
  )
*/

  //setUp()
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


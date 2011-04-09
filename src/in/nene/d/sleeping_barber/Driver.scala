package in.nene.d.sleeping_barber

import akka.config.Supervision.Supervise
import in.nene.d.sleeping_barber.actors.Barber
import in.nene.d.sleeping_barber.actors.WaitingRoom
import in.nene.d.sleeping_barber.domain.Customer
import akka.actor.Actor.registry
import akka.actor.SupervisorFactory
import akka.config.Supervision.SupervisorConfig
import akka.config.Supervision.OneForOneStrategy
import akka.config.Supervision.Permanent

import akka.actor.Actor.actorOf

object Driver {
	def main(args : Array[String]) : Unit = {
		val barber = actorOf(new Barber())
		val waitingRoom = actorOf(new WaitingRoom(3))
		val supervisor = SupervisorFactory(
							SupervisorConfig(
							OneForOneStrategy(List(classOf[Exception]), 3, 10),
							Supervise(waitingRoom, Permanent) :: 
								Supervise(barber, Permanent) :: 
								Nil)).newInstance

		supervisor.start
		println("----------------")
		Thread.sleep(2000)
		for(actorRef <-  registry.actors)
			println(actorRef)
        // Note: Passing a reference to an actor at startup
        // is not good enough since it will be invalid when the
        // supervisor restarts a failed actor
		waitingRoom ! barber
		for(i <- 1 to 20) {
			waitingRoom ! new Customer(i)
			Thread.sleep(1)
		}
		Thread.sleep(10000)
		supervisor.shutdown()
  }
}

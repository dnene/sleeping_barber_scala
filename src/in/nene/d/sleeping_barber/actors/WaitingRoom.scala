package in.nene.d.sleeping_barber.actors

import in.nene.d.sleeping_barber.actors.Barber
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor.registry
import in.nene.d.sleeping_barber.domain.Customer
import scala.collection.mutable.Queue

class WaitingRoom (val size: Int) extends Actor {
	var queue = new Queue[Customer]
	// Would've preferred the following line to work .. but it gets initialised a little later
	// var barber: ActorRef = registry.actorsFor("in.nene.d.sleeping_barber.actors.Barber")(0)
	
	// Not sure how to handle this deferred initialisation of the barber actor in an idiomatic scala way
	var _barber: ActorRef = null
	def barber =
		if (_barber != null) _barber else {
			_barber =  registry.actorsFor("in.nene.d.sleeping_barber.actors.Barber")(0)	
			_barber
		}
	
	override def receive = {
		case customer: Customer => {
			if (queue.length <= 2) {
				println("Enqueueing customer " + customer)
				queue.enqueue(customer)
				barber ! "notify"
			}
			else {
				println("Denying service to customer " + customer)
			}
		} 
		case "next" => {
			println("Got request for next")
			val next = queue.dequeue()
			self.sender.get ! next
		}
		case unknown_message => {
			throw new IllegalArgumentException("Unknown message: " + unknown_message)
		}
	}
}
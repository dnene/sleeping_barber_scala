package in.nene.d.sleeping_barber.actors

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor.registry
import in.nene.d.sleeping_barber.domain.Customer

class Barber () extends Actor {
	def receive = {
		case "notify" => {
			println("Got notified. Now pulling a customer")
			self.reply("next")
		}
		case customer: Customer => {
			println("Now processing customer " + customer)
		}
		case unknown_message => println("Ignoring unknown message:" + unknown_message)
	}
}
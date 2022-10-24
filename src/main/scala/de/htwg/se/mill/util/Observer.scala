package de.htwg.se.mill.util

trait Observer {
  def update(message: Option[String]): Unit
}

trait Observable {
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = {
    subscribers = subscribers.appended(s)
  }
  def remove(s: Observer) = {
    subscribers = subscribers.filterNot(o => o == s)
  }
  def notifyObservers(message: Option[String]) =
    subscribers.foreach(o => o.update(message))
}

package de.htwg.se.mill.util

trait Observer {
  def update(error: Option[Throwable]): Unit
}

trait Observable {
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = {
    subscribers = subscribers.appended(s)
  }
  def remove(s: Observer) = {
    subscribers = subscribers.filterNot(o => o == s)
  }
  def notifyObservers(error: Option[Throwable]) =
    subscribers.foreach(o => o.update(error))
}

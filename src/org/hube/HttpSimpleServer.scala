/**
 * Copyright 2011 Alberto Franco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hube

import scala.actors.Actor

import java.net.Socket
import java.net.ServerSocket;
import java.net.InetAddress


class HttpSimpleServer(port:Int, address:InetAddress) extends Actor {
  
  val backlog:Int = 10; // Make backlog queue length constant.
  
  //-- Net parameters listen port and server socket ---------------------------- 
  private var listenPort:Int = port;
  private var server:ServerSocket = new ServerSocket(port, backlog, address);
  
  //-- Handler for requests: create and start ----------------------------------
  private var handler:RequestHandler = new RequestHandler;
  handler.start();
  
  def addHandler(url:String, handle: ResponseHandler){
    handler.addHandler(url, handle)
  }
  
  /**
   * Act method. Receive requests from the net and dispatch to request handler
   * actor. This is the facade of the web server.
   */
  def act() {
	  loop{
	    // -- Receive request and pass it to the handler -----------------------
	    var socket:Socket = server.accept();
	    handler ! socket;
	  }
  }
  
}
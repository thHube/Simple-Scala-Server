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

/**
 * Handle a response to the client. This is the actor responsable of writing 
 * back server response.
 */
class ResponseHandler(address:String) extends Actor {
  /**
   * Handle responses, every requests arrive is resolved with the call of 
   * reply method that is a kind of delegate. We use receive and not react 
   * to avoid mailbox filling.   
   */
  def act() {
    loop { 
      receive {
        // -- A tuple is passed to worker actor --------------------------------
        case (req:HttpRequest, resp:HttpResponseWriter) => {
          // -- Reply the request and close connection -------------------------
          reply(req, resp);
          resp.close(200);
        }
        // -- Discard all other request ----------------------------------------
        case _ => Unit;
      }
    }
  }
  
  /**
   * Reply to the client with a simple message.
   * @param req HttpRequest that arrives to the server.
   * @param resp The response to send to the client.  
   */
  def reply(req:HttpRequest, resp:HttpResponseWriter){
	println("Handler for " + address + " reply!");
	resp.println("Hello from" + address);    
  }
}

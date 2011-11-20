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
import scala.collection.parallel.mutable.ParHashMap;

import java.net.Socket
import java.io.BufferedReader

/**
 * Request handler is an actor that receive requests from the facade and 
 * distribute to worker actors for single URLs.
 */
class RequestHandler extends Actor {

  private var handlers:ParHashMap[String, ResponseHandler] = 
    new ParHashMap[String, ResponseHandler];
  
  // -- Help function for debug prints -----------------------------------------
  private def printmsg(msg:String) = println("[Scala Server] >> " + msg);

  /**
   * Add a response handler to current worker. This method also start the
   * request handler.
   * @param url Url to bind the request handler.
   * @param handler The handler to bind. 
   */
  def addHandler(url:String, handler:ResponseHandler) {
    handlers += url -> handler;
    handler.start();
  }
  
  /**
   * Main handler method. This runs as a thread: receive and serve requests. 
   */
  def act() {
    printmsg("Starting server...");
    // -- Loop forever until shutdown ------------------------------------------
    loop {
      receive {
        // -- In case is a socket manage socket connection ---------------------
        case client:Socket => {
          try {
            printmsg("Received connection " + client.getInetAddress() + 
                ":" + client.getPort());
            var req:HttpRequest = new HttpRequest(client.getInputStream());
            var resp:HttpResponseWriter = 
              new HttpResponseWriter(client.getOutputStream()); 
            // -- Send data to the correct handler -----------------------------
            val handle:ResponseHandler = handlers(req.getRequest());
            if (handle != null) {
              handle ! (req, resp);
            } else {
              resp.close(404);
            }
          } catch {
            // -- Handle exception ---------------------------------------------
            case e:Exception => {
              // -- This avoids crash for silly motivation such as favicon    --
              // -- request that is not satisfied unless we bind the url to a --
              // -- static file.                                              --
              printmsg("An exception has occurred!");
              e.printStackTrace();
            }
          }
        }
        // -- Any other case is discarded not to fill mailbox ------------------
        case _ => printmsg("Request error, discarded");
      }
    }
  } 
  
}

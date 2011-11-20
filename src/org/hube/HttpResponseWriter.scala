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

import java.io.OutputStream;
import java.io.DataOutputStream

/**
 * A response writer handles HTTP response of the server to the client. Each
 * line of text is written to the response once response is closed.  
 */
class HttpResponseWriter(outputStream:OutputStream) { 
  // -- Private params ---------------------------------------------------------
  private var out = new DataOutputStream(outputStream);
  private var response:String = "";
  
  /**
   * Write to the response message you gave.
   * @param msg Message to write in the response
   */
  def println(msg:String) {
    response += msg + " \n";
  }
  
  /**
   * Close connection with the client and write data.
   * @param status HTTP status of the response. 
   */
  def close(status:Int) {
    var httpResponse:String  = "";
    
  	// -- Reply with HTTP code -------------------------------------------------
    if (status == 200){
      httpResponse += "HTTP/1.1 200 OK" + "\r\n";
    } else {
      httpResponse += "HTTP/1.1 404 Not Found" + "\r\n";
    }
    
    httpResponse += "Server:Scala HTTP Simple-Server\r\n";
  	httpResponse += "Content-Type:text/html\r\n";
  	httpResponse += "Content-Length: " + response.length() + "\r\n";
  	httpResponse += "Connection: close \r\n\r\n";
    httpResponse += response;
  	
    // -- Write bytes in the response ------------------------------------------
    out.writeBytes(httpResponse);
    out.close();
  }
}
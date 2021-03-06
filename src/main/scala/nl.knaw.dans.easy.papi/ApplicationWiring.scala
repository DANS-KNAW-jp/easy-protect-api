/**
 * Copyright (C) 2017 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy.papi

import nl.knaw.dans.easy.papi.components.LdapAuthentication
import nl.knaw.dans.lib.logging.DebugEnhancedLogging

/**
 * Initializes and wires together the components of this application.
 *
 * @param configuration the application configuration
 */
class ApplicationWiring(configuration: Configuration) extends DebugEnhancedLogging
  with LdapAuthentication {

  override val authentication: Authentication = new Authentication {
    override val ldapUsersEntry: String = configuration.properties.getString("ldap.users-entry")
    override val ldapProviderUrl: String = configuration.properties.getString("ldap.provider.url")
  }
}

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
package nl.knaw.dans.easy.papi.components

import java.util
import javax.naming.directory.{ Attribute, SearchControls, SearchResult }
import javax.naming.ldap.{ InitialLdapContext, LdapContext }
import javax.naming.{ AuthenticationException, Context }

import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import org.apache.commons.lang.StringUtils
import resource.managed

import scala.collection.JavaConverters._
import scala.util.{ Failure, Success, Try }

trait LdapAuthentication extends DebugEnhancedLogging {

  val authentication: AuthenticationProvider

  trait Authentication extends AuthenticationProvider {
    val ldapUsersEntry: String
    val ldapProviderUrl: String

    def getUser(userName: String, password: String): Try[Option[User]] = {
      logger.debug(s"looking for user [$userName]")

      val query = s"(&(objectClass=easyUser)(uid=$userName))"
      val connectionProperties = new util.Hashtable[String, String]() {
        put(Context.PROVIDER_URL, ldapProviderUrl)
        put(Context.SECURITY_AUTHENTICATION, "simple")
        put(Context.SECURITY_PRINCIPAL, s"uid=$userName, $ldapUsersEntry")
        put(Context.SECURITY_CREDENTIALS, password)
        put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
      }
      val searchControls = new SearchControls() {
        setSearchScope(SearchControls.SUBTREE_SCOPE)
      }

      if (StringUtils.isBlank(userName) || StringUtils.isBlank(password))
        Failure(new IllegalArgumentException("user-name nor password should be blank"))
      else managed(getContext(connectionProperties))
        .map(_
          .search(ldapUsersEntry, query, searchControls)
          .asScala.toList.headOption
          .map(searchResult => User(toMap(searchResult)))
        ).tried.map {
        case Some(user) if !user.isActive => None
        case maybeUser => maybeUser
      }.recoverWith {
        case _: AuthenticationException => Success(None)
        case t => Failure(t)
      }
    }

    private def toMap(searchResult: SearchResult) = {
      searchResult.getAttributes.getAll.asScala.map(toTuple).toMap
    }

    private def toTuple(attribute: Attribute): (String, Seq[String]) = {
      (attribute.getID, attribute.getAll.asScala.map(_.toString).toSeq)
    }

    protected def getContext(connectionProperties: util.Hashtable[String, String]): LdapContext = {
      new InitialLdapContext(connectionProperties, null)
    }
  }
}

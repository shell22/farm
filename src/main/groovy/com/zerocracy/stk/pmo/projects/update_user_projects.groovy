/*
 * Copyright (c) 2016-2019 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.stk.pmo.projects

import com.jcabi.xml.XML
import com.zerocracy.Farm
import com.zerocracy.Project
import com.zerocracy.entry.ClaimsOf
import com.zerocracy.farm.Assume
import com.zerocracy.claims.ClaimIn
import com.zerocracy.pm.staff.Roles
import com.zerocracy.pmo.Projects

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type(
    'Role was assigned',
    'Order was given',
    'All roles were resigned',
    'Role was resigned'
  )
  ClaimIn claim = new ClaimIn(xml)
  String login = claim.param('login')
  Farm farm = binding.variables.farm
  Projects projects = new Projects(farm, login).bootstrap()
  Roles roles = new Roles(project).bootstrap()
  if (roles.hasAnyRole(login) && !projects.exists(project.pid())) {
    new Projects(farm, login).remove(project.pid())
    projects.add(project.pid())
    claim.copy()
      .type('User joined new project')
      .param('login', login)
      .postTo(new ClaimsOf(farm, project))
  }
  if (!roles.hasAnyRole(login) && projects.exists(project.pid())) {
    projects.remove(project.pid())
    claim.copy()
      .type('User left a project')
      .param('login', login)
      .postTo(new ClaimsOf(farm, project))
  }
}

/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.ckeditor.test.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.xwiki.test.docker.junit5.TestReference;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.ckeditor.test.po.AutocompleteDropdown;

/**
 * Integration tests for localization support.
 * 
 * XWiki's localization support in CKEditor affects all XWiki's
 * custom CKEditor plugins. QuickActions is only one of the
 * affected features.
 * 
 * @version $Id$
 * @since 15.5.4
 * @since 15.9.1
 * @since 15.10RC1
 */
@UITest(
    properties = {
        "xwikiDbHbmCommonExtraMappings=notification-filter-preferences.hbm.xml"
    },
    extraJARs = {
        // It's currently not possible to install a JAR contributing a Hibernate mapping file as an Extension. Thus
        // we need to provide the JAR inside WEB-INF/lib. See https://jira.xwiki.org/browse/XWIKI-8271
        "org.xwiki.platform:xwiki-platform-notifications-filters-default",

        // The macro service uses the extension index script service to get the list of uninstalled macros (from
        // extensions) which expects an implementation of the extension index. The extension index script service is a
        // core extension so we need to make the extension index also core.
        "org.xwiki.platform:xwiki-platform-extension-index",
        // Solr search is used to get suggestions for the link quick action.
        "org.xwiki.platform:xwiki-platform-search-solr-query"
    },
    resolveExtraJARs = true
)
class LocalizationIT extends AbstractCKEditorIT
{
    
    @BeforeAll
    void beforeAll(TestUtils setup)
    {
        setup.loginAsSuperAdmin();
        
        // Make WYSIWYG the default editor
        setup.setPropertyInXWikiPreferences("editor", "String", "Wysiwyg");
    }

    @AfterEach
    void afterEach(TestUtils setup)
    {
        setup.maybeLeaveEditMode();
        setup.setPropertyInXWikiPreferences("default_language", "String", "en");
    }

    @Test
    @Order(1)
    void paragraphQuickAction_en(TestUtils setup, TestReference testReference) {
        setup.setPropertyInXWikiPreferences("default_language", "String", "en");
        edit(setup, testReference);
        
        // We check the paragraph Quick Action because it is registered
        // with a CKEditor Integration Translation key.
        textArea.sendKeys("/p");
        new AutocompleteDropdown().waitForItemSelected("/p", "Paragraph");
    }
    
    @Test
    @Order(2)
    void paragraphQuickAction_de_DE(TestUtils setup, TestReference testReference) {
        
        setup.setPropertyInXWikiPreferences("default_language", "String", "de_DE");
        edit(setup, testReference);
        
        // We check the paragraph Quick Action because it is registered
        // with a CKEditor Integration Translation key.
        textArea.sendKeys("/abs");
        new AutocompleteDropdown().waitForItemSelected("/abs", "Absatz");
    }
}

/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.hummingbird.uitest.ui;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.hummingbird.testutil.PhantomJSTest;

public class ScriptInjectIT extends PhantomJSTest {

    @Test
    public void ensureNoAlerts() {
        open();
        List<WebElement> inputs = findElements(By.xpath("//input"));
        Assert.assertEquals(ScriptInjectView.values.length, inputs.size());

        // All inputs should contain some variant of
        // <script>alert('foo');</script>
        for (int i = 0; i < inputs.size(); i++) {
            WebElement e = inputs.get(i);
            Assert.assertEquals(
                    ScriptInjectView.getValue(ScriptInjectView.values[i]),
                    e.getAttribute("value"));
        }

    }
}
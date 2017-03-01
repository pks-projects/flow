/*
 * Copyright 2000-2017 Vaadin Ltd.
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

package com.vaadin.hummingbird.nodefeature;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.hummingbird.StateNode;
import com.vaadin.hummingbird.StateNodeTest;
import com.vaadin.hummingbird.nodefeature.PushConfigurationMap.PushConfigurationParametersMap;
import com.vaadin.hummingbird.shared.NodeFeatures;

public class NodeFeatureTest {
    private static abstract class UnregisteredNodeFeature extends NodeFeature {
        public UnregisteredNodeFeature(StateNode node) {
            super(node);
        }
    }

    @Test(expected = AssertionError.class)
    public void testCreateNullTypeThrows() {
        NodeFeatureRegistry.create(null, StateNodeTest.createEmptyNode());
    }

    @Test(expected = AssertionError.class)
    public void testCreateNullNodeThrows() {
        NodeFeatureRegistry.create(ElementData.class, null);
    }

    @Test(expected = AssertionError.class)
    public void testCreateUnknownFeatureThrows() {
        NodeFeatureRegistry.create(UnregisteredNodeFeature.class,
                StateNodeTest.createEmptyNode());
    }

    @Test
    public void testGetIdValues() {
        // Verifies that the ids are the same as on the client side
        Map<Class<? extends NodeFeature>, Integer> expectedIds = new HashMap<>();

        expectedIds.put(ElementData.class, NodeFeatures.ELEMENT_DATA);
        expectedIds.put(ElementPropertyMap.class,
                NodeFeatures.ELEMENT_PROPERTIES);
        expectedIds.put(ElementAttributeMap.class,
                NodeFeatures.ELEMENT_ATTRIBUTES);
        expectedIds.put(ElementChildrenList.class,
                NodeFeatures.ELEMENT_CHILDREN);
        expectedIds.put(ElementListenerMap.class,
                NodeFeatures.ELEMENT_LISTENERS);
        expectedIds.put(PushConfigurationMap.class,
                NodeFeatures.UI_PUSHCONFIGURATION);
        expectedIds.put(PushConfigurationParametersMap.class,
                NodeFeatures.UI_PUSHCONFIGURATION_PARAMETERS);
        expectedIds.put(TextNodeMap.class, NodeFeatures.TEXT_NODE);
        expectedIds.put(PollConfigurationMap.class,
                NodeFeatures.POLL_CONFIGURATION);
        expectedIds.put(ReconnectDialogConfigurationMap.class,
                NodeFeatures.RECONNECT_DIALOG_CONFIGURATION);
        expectedIds.put(LoadingIndicatorConfigurationMap.class,
                NodeFeatures.LOADING_INDICATOR_CONFIGURATION);
        expectedIds.put(ElementClassList.class, NodeFeatures.CLASS_LIST);
        expectedIds.put(ElementStylePropertyMap.class,
                NodeFeatures.ELEMENT_STYLE_PROPERTIES);
        expectedIds.put(SynchronizedPropertiesList.class,
                NodeFeatures.SYNCHRONIZED_PROPERTIES);
        expectedIds.put(SynchronizedPropertyEventsList.class,
                NodeFeatures.SYNCHRONIZED_PROPERTY_EVENTS);
        expectedIds.put(ComponentMapping.class, NodeFeatures.COMPONENT_MAPPING);
        expectedIds.put(TemplateMap.class, NodeFeatures.TEMPLATE);
        expectedIds.put(ModelMap.class, NodeFeatures.TEMPLATE_MODELMAP);
        expectedIds.put(TemplateOverridesMap.class,
                NodeFeatures.TEMPLATE_OVERRIDES);
        expectedIds.put(OverrideElementData.class, NodeFeatures.OVERRIDE_DATA);
        expectedIds.put(ParentGeneratorHolder.class,
                NodeFeatures.PARENT_GENERATOR);
        expectedIds.put(ModelList.class, NodeFeatures.TEMPLATE_MODELLIST);
        expectedIds.put(PublishedServerEventHandlers.class,
                NodeFeatures.PUBLISHED_SERVER_EVENT_HANDLERS);
        expectedIds.put(PolymerServerEventHandlers.class,
                NodeFeatures.POLYMER_SERVER_EVENT_HANDLERS);

        Assert.assertEquals("The number of expected features is not up to date",
                expectedIds.size(), NodeFeatureRegistry.nodeFeatures.size());

        expectedIds.forEach((type, expectedId) -> {
            Assert.assertEquals("Unexpected id for " + type.getName(),
                    expectedId.intValue(), NodeFeatureRegistry.getId(type));
        });
    }
}

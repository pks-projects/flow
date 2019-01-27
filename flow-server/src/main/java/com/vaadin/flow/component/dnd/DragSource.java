/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.flow.component.dnd;

import java.util.concurrent.atomic.AtomicReference;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;

import elemental.json.JsonObject;

public interface DragSource<T extends Component> extends HasElement {

    String ACTIVE_DRAG_SOURCE_CLASS_NAME = "v-dragged";
    String DATA_TRANSFER_DROP_EFFECT_PROPERTY = "event.dataTransfer.dropEffect";
    String EFFECT_ALLOWED_ELEMENT_PROPERTY = "_effectAllowed";

    //@formatter:off
    String DRAG_START_EVENT = "event.stopPropagation();"
            + "var e = event.currentTarget;"
            + "e.classList.add('"
            + DragSource.ACTIVE_DRAG_SOURCE_CLASS_NAME + "');"
            + "var effect = e['" + EFFECT_ALLOWED_ELEMENT_PROPERTY + "'] ?"
            + "e['" + EFFECT_ALLOWED_ELEMENT_PROPERTY + "'] : 'all';"
            + "event.dataTransfer.effectAllowed = effect;"
            + "event.dataTransfer.setData('text/plain', 'foobar');"
            + "return true;";
    
    String DRAG_END_EVENT =
            "event.currentTarget.classList.remove('"+ACTIVE_DRAG_SOURCE_CLASS_NAME+"');"
            + "return true;";
    //@formatter:on

    static <T extends Component> DragSource<T> of(T component) {
        DragSource<T> dragSource = new DragSource<T>() {
            @Override
            public T getDragSourceComponent() {
                return component;
            }
        };
        dragSource.setDraggable(true);
        return dragSource;
    }

    T getDragSourceComponent();

    @Override
    default Element getElement() {
        return getDragSourceComponent().getElement();
    }

    default void setDraggable(boolean draggable) {
        if (draggable == isDraggable()) {
            return;
        }
        if (draggable) {
            // The attribute is an enumerated one and not a Boolean one.
            getElement().setProperty("draggable", "true");
            initListeners(this);
        } else {
            getElement().removeProperty("draggable");
        }
    }

    default boolean isDraggable() {
        return getElement().hasProperty("draggable");
    }

    /**
     * Set server side drag data. This data is available in the drop event and
     * can be used to transfer data between drag source and drop target if they
     * are in the same UI.
     *
     * @param data
     *            Data to transfer to drop event.
     */
    default void setDragData(Object data) {
        ComponentUtil.setData(getDragSourceComponent(), "drag-source-data",
                data);
    }

    /**
     * Get server side drag data. This data is available in the drop event and
     * can be used to transfer data between drag source and drop target if they
     * are in the same UI.
     *
     * @return Server side drag data if set, otherwise {@literal null}.
     */
    default Object getDragData() {
        return ComponentUtil.getData(getDragSourceComponent(),
                "drag-source" + "-data");
    }

    /**
     * Sets the allowed effects for the current drag source element. Used for
     * setting client side {@code DataTransfer.effectAllowed} parameter for the
     * drag event.
     * <p>
     * By default the value is {@link EffectAllowed#UNINITIALIZED} which is
     * equivalent to {@link EffectAllowed#ALL}.
     *
     * @param effect
     *            Effects to allow for this draggable element. Cannot be {@code
     *               null}.
     */
    default void setEffectAllowed(EffectAllowed effect) {
        if (effect == null) {
            throw new IllegalArgumentException("Allowed effect cannot be null");
        }
        getElement().setProperty(EFFECT_ALLOWED_ELEMENT_PROPERTY,
                effect.getValue());
    }

    /**
     * Returns the allowed effects for the current drag source element. Used to
     * set client side {@code DataTransfer.effectAllowed} parameter for the drag
     * event.
     * <p>
     * You can use different types of data to support dragging to different
     * targets. Accepted types depend on the drop target and those can be
     * platform specific. See
     * https://developer.mozilla.org/en-US/docs/Web/API/HTML_Drag_and_Drop_API/Recommended_drag_types
     * for examples on different types.
     * <p>
     * <em>NOTE: IE11 only supports type ' text', which can be set using
     * {@link #setDataTransferText(String data)}</em>
     *
     * @return Effects that are allowed for this draggable element.
     */
    default EffectAllowed getEffectAllowed() {
        return EffectAllowed.valueOf(getElement().getProperty(
                EFFECT_ALLOWED_ELEMENT_PROPERTY, EffectAllowed.ALL.getValue()));
    }

    /**
     * Attaches dragstart listener for the current drag source.
     * {@link DragStartListener#dragStart(DragStartEvent)} is called when
     * dragstart event happens on the client side.
     *
     * @param listener
     *            Listener to handle dragstart event.
     * @return Handle to be used to remove this listener.
     */
    default Registration addDragStartListener(DragStartListener<T> listener) {
        return ComponentUtil.addListener(getDragSourceComponent(),
                DragStartEvent.class, (ComponentEventListener) listener);
    }

    /**
     * Attaches dragend listener for the current drag source.
     * {@link DragEndListener#dragEnd(DragEndEvent)} is called when dragend
     * event happens on the client side.
     *
     * @param listener
     *            Listener to handle dragend event.
     * @return Handle to be used to remove this listener.
     */
    default Registration addDragEndListener(DragEndListener<T> listener) {
        return ComponentUtil.addListener(getDragSourceComponent(),
                DragEndEvent.class, (ComponentEventListener) listener);
    }

    static <T extends Component> void initListeners(DragSource<T> dragSource) {
        Element element = dragSource.getElement();
        Command lazyRegisterListeners = () -> {
            UI activeUI = UI.getCurrent();
            T component = dragSource.getDragSourceComponent();

            DomListenerRegistration startRegistration = element
                    .addEventListener("dragstart", event -> {
                        activeUI.setActiveDragSource(dragSource);
                        ComponentUtil.fireEvent(component,
                                new DragStartEvent<>(dragSource, component));
                    }).setFilter("element._ondragstart(event)");
            element.executeJavaScript("this._ondragstart = function(event) "
                    + "{" + DRAG_START_EVENT + "}");

            DomListenerRegistration endRegistration = element
                    .addEventListener("dragend", event -> {
                        JsonObject eventData = event.getEventData();
                        DropEffect dropEffect = DropEffect.NONE;
                        if (eventData
                                .hasKey(DATA_TRANSFER_DROP_EFFECT_PROPERTY)) {
                            dropEffect = DropEffect.valueOf(eventData
                                    .getString(
                                            DATA_TRANSFER_DROP_EFFECT_PROPERTY)
                                    .toUpperCase());
                        }
                        ComponentUtil.fireEvent(component,
                                new DragEndEvent<T>(dragSource, component,
                                        dropEffect));
                        activeUI.setActiveDragSource(null);
                    }).addEventData(DATA_TRANSFER_DROP_EFFECT_PROPERTY)
                    .setFilter("element._ondragend(event)");
            element.executeJavaScript("this._ondragend = function(event) " + "{"
                    + DRAG_END_EVENT + "}");

            // remove the registered listeners if dragging is disabled
            // later on
            AtomicReference<Registration> propertyChangeRegistration = new AtomicReference<>();
            propertyChangeRegistration.set(
                    element.addPropertyChangeListener("draggable", event -> {
                        startRegistration.remove();
                        endRegistration.remove();
                        propertyChangeRegistration.get().remove();
                    }));
        };
        if (!element.getNode().isAttached()) {
            AtomicReference<Registration> attachRegistration = new AtomicReference<>();
            attachRegistration.set(element.addAttachListener(attachEvent -> {
                lazyRegisterListeners.execute();
                attachRegistration.get().remove();
            }));
        } else {
            lazyRegisterListeners.execute();
        }
    }
}

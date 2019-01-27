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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;

/**
 * @author Vaadin Ltd
 */
public class DragSourceComponent<T extends Component> extends Composite<T> implements DragSource<T> {

    private final T origin;

    private Registration dragStartListenerHandle;
    private Registration dragEndListenerHandle;

    private final Map<String, String> transferData = new LinkedHashMap<>();

    private final Map<String, Payload> payloads = new HashMap<>();

    /**
     * Stores the server side drag data that is available for the drop target if
     * it is in the same UI.
     */
    private Object dragData;

    public DragSourceComponent(T component) {
        origin = component;
        setDraggable(true);
    }

    @Override
    public T getDragSourceComponent() {
        return getContent();
    }

    /**
     * Sets data for this drag source element with the given type. The data is
     * set for the client side draggable element using {@code
     * DataTransfer.setData(type, data)} method.
     * <p>
     * Note that {@code "text"} is the only cross browser supported data type.
     * Use {@link #setDataTransferText(String)} method instead if your
     * application supports IE11.
     *
     * @param type Type of the data to be set for the client side draggable
     *             element, e.g. {@code text/plain}. Cannot be {@code null}.
     * @param data Data to be set for the client side draggable element. Cannot
     *             be {@code null}.
     */
    public void setDataTransferData(String type, String data) {
        if (type == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }

        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        transferData.put(type, data);
    }

    /**
     * Returns the data stored with type {@code type} in this drag source
     * element.
     *
     * @param type Type of the requested data, e.g. {@code text/plain}.
     * @return Data of type {@code type} stored in this drag source element.
     */
    public String getDataTransferData(String type) {
        return transferData.get(type);
    }

    /**
     * Returns the map of data stored in this drag source element. The returned
     * map preserves the order of storage and is unmodifiable.
     *
     * @return Unmodifiable copy of the map of data in the order the data was
     * stored.
     */
    public Map<String, String> getDataTransferData() {
        return Collections.unmodifiableMap(transferData);
    }

    /**
     * Sets data of type {@code "text"} for this drag source element. The data
     * is set for the client side draggable element using the {@code
     * DataTransfer.setData("text", data)} method.
     * <p>
     * Note that {@code "text"} is the only cross browser supported data type.
     * Use this method if your application supports IE11.
     *
     * @param data Data to be set for the client side draggable element.
     * @see #setDataTransferData(String, String)
     */
    public void setDataTransferText(String data) {
        setDataTransferData(DnDConstants.DATA_TYPE_TEXT, data);
    }

    /**
     * Returns the data stored with type {@code "text"} in this drag source
     * element.
     *
     * @return Data of type {@code "text"} stored in this drag source element.
     */
    public String getDataTransferText() {
        return getDataTransferData(DnDConstants.DATA_TYPE_TEXT);
    }

    /**
     * Clears data with the given type for this drag source element when
     * present.
     *
     * @param type Type of data to be cleared. Cannot be {@code null}.
     */
    public void clearDataTransferData(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }

        transferData.remove(type);
    }

    /**
     * Clears all data for this drag source element.
     */
    public void clearDataTransferData() {
        transferData.clear();
    }

    /**
     * Sets payload for this drag source to use with acceptance criterion. The
     * payload is transferred as data type in the data transfer object in the
     * following format: {@code "v-item:string:key:value"}. The given value is
     * compared to the criterion value when the drag source is dragged on top of
     * a drop target that has the suitable criterion.
     * <p>
     * Note that setting payload in Internet Explorer 11 is not possible due to
     * the browser's limitations.
     *
     * @param key   key of the payload to be transferred
     * @param value value of the payload to be transferred
     * @see DropTargetComponent#setDropCriterion(String, String)
     */
    public void setPayload(String key, String value) {
        setPayload(key, String.valueOf(value), Payload.ValueType.STRING);
    }

    /**
     * Sets payload for this drag source to use with acceptance criterion. The
     * payload is transferred as data type in the data transfer object in the
     * following format: {@code "v-item:integer:key:value"}. The given value is
     * compared to the criterion value when the drag source is dragged on top of
     * a drop target that has the suitable criterion.
     * <p>
     * Note that setting payload in Internet Explorer 11 is not possible due to
     * the browser's limitations.
     *
     * @param key   key of the payload to be transferred
     * @param value value of the payload to be transferred
     * @see DropTargetComponent#setDropCriterion(String,
     * com.vaadin.flow.component.dnd.ComparisonOperator, int)
     * DropTargetComponent#setDropCriterion(String, ComparisonOperator,
     * int)
     */
    public void setPayload(String key, int value) {
        setPayload(key, String.valueOf(value), Payload.ValueType.INTEGER);
    }

    /**
     * Sets payload for this drag source to use with acceptance criterion. The
     * payload is transferred as data type in the data transfer object in the
     * following format: {@code "v-item:double:key:value"}. The given value is
     * compared to the criterion value when the drag source is dragged on top of
     * a drop target that has the suitable criterion.
     * <p>
     * Note that setting payload in Internet Explorer 11 is not possible due to
     * the browser's limitations.
     *
     * @param key   key of the payload to be transferred
     * @param value value of the payload to be transferred
     * @see DropTargetComponent#setDropCriterion(String,
     * com.vaadin.flow.component.dnd.ComparisonOperator, double)
     * DropTargetComponent#setDropCriterion(String, ComparisonOperator,
     * double)
     */
    public void setPayload(String key, double value) {
        setPayload(key, String.valueOf(value), Payload.ValueType.DOUBLE);
    }

    private void setPayload(String key, String value,
                            Payload.ValueType valueType) {
        payloads.put(key, new Payload(key, value, valueType));
    }

    /**
     * Set a custom drag image for the current drag source.
     * <p>
     * TODO : what type should be {@code imageResource} ? In FW8 it's a
     * {@code Resource} which doesn't exist in Flow. MAy be it should be just a
     * {@code String} ?
     *
     * @param imageResource Resource of the image to be displayed as drag image.
     */
    public void setDragImage(Component imageResource) {
    }

    @Override
    protected T initContent() {
        return origin;
    }

}

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * his work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.amqp.protocol.types;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.Long;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.AmqpHandle;
import org.apache.activemq.amqp.protocol.types.AmqpUint;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a the handle of a Link
 * <p>
 * command and subsequently used
 * by endpoints as a shorthand to refer to the Link in all outgoing commands. The two
 * endpoints may potentially use different handles to refer to the same Link. Link handles
 * may be reused once a Link is closed for both send and receive.
 * </p>
 */
public interface AmqpHandle extends AmqpUint {


    public static class AmqpHandleBean implements AmqpHandle{

        private AmqpHandleBuffer buffer;
        private AmqpHandleBean bean = this;
        private Long value;

        AmqpHandleBean(Long value) {
            this.value = value;
        }

        AmqpHandleBean(AmqpHandle.AmqpHandleBean other) {
            this.bean = other;
        }

        public final AmqpHandleBean copy() {
            return bean;
        }

        public final AmqpHandle.AmqpHandleBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpHandleBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public Long getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpHandle)) {
                return false;
            }

            return equals((AmqpHandle) o);
        }

        public boolean equals(AmqpHandle b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpHandle.AmqpHandleBean.class.hashCode();
            }
            return getValue().hashCode();
        }
    }

    public static class AmqpHandleBuffer extends AmqpUint.AmqpUintBuffer implements AmqpHandle{

        private AmqpHandleBean bean;

        protected AmqpHandleBuffer() {
            super();
        }

        protected AmqpHandleBuffer(Encoded<Long> encoded) {
            super(encoded);
        }

        public Long getValue() {
            return bean().getValue();
        }

        public AmqpHandle.AmqpHandleBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpHandle bean() {
            if(bean == null) {
                bean = new AmqpHandle.AmqpHandleBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpHandle o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpHandle.AmqpHandleBuffer create(Encoded<Long> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpHandle.AmqpHandleBuffer(encoded);
        }

        public static AmqpHandle.AmqpHandleBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpUint(in));
        }

        public static AmqpHandle.AmqpHandleBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpUint(buffer, offset));
        }
    }
}
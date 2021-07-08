/*
 * Copyright 2012 - 2018 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.protocol;

import org.traccar.BaseFrameDecoder;
import org.traccar.helper.BufferUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class XexunFrameDecoder extends BaseFrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ByteBuf buf) throws Exception {

        String str;
        if(buf.hasArray()) { // handle heap buffer
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // handle direct buffers and composite buffers
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        System.out.println("###########################");
        System.out.println("Frame: " + str);
        int beginIndex, endIndex;
        if(BufferUtil.indexOf("powercar", buf) > -1){
            System.out.println(" > RESPONSE COMMAND");
            return buf;
        }
        if (buf.readableBytes() < 80) {
            System.out.println(" >1");
            return null;
        }

        beginIndex = BufferUtil.indexOf("GPRMC", buf);
        if (beginIndex == -1) {
            beginIndex = BufferUtil.indexOf("GNRMC", buf);
            if (beginIndex == -1) {
                System.out.println(" >2");
                return null;
            }
        }

        int identifierIndex = BufferUtil.indexOf("imei:", buf, beginIndex, buf.writerIndex());
        if (identifierIndex == -1) {
            System.out.println(" >3");
            return null;
        }

        endIndex = buf.indexOf(identifierIndex, buf.writerIndex(), (byte) ',');
        if (endIndex == -1) {
            System.out.println(" >4");
            return null;
        }
        System.out.println(" >5");
        buf.skipBytes(beginIndex - buf.readerIndex());

        return buf.readRetainedSlice(endIndex - beginIndex + 1);
    }

}

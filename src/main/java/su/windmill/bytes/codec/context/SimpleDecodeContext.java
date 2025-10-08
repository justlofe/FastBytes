package su.windmill.bytes.codec.context;

import su.windmill.bytes.buffer.FastBuffer;

record SimpleDecodeContext(FastBuffer buffer) implements DecodeContext {

}

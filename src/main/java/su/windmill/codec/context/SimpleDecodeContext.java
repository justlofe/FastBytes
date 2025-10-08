package su.windmill.codec.context;

import su.windmill.buffer.FastBuffer;

record SimpleDecodeContext(FastBuffer buffer) implements DecodeContext {

}

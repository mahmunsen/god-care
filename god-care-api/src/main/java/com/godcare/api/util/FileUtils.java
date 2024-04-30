package com.godcare.api.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.List;

@Slf4j
@UtilityClass // 해당 클래스의 모든 메서드가 static으로 선언, 기본 생성자가 private으로 선언됨
public class FileUtils {

    public ByteBuffer getByteBuffer(ByteBuffer buffer) {
        log.info("Creating ByteBuffer from {} chunks", buffer.capacity());

        int partSize = 0;
        partSize += (buffer.capacity() - buffer.remaining()); // 전체 사이즈 - 나머지 사이즈 = 현재 크기

        ByteBuffer partData = ByteBuffer.allocate(partSize);
        partData.put(buffer); // put(): 현재 위치에 데이터 쓰기

        partData.rewind(); // rewind(): 버퍼 위치를 0으로 설정

        log.info("PartData: capacity={}", partData.capacity());
        return partData;
    }
}

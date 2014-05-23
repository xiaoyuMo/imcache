/*
* Copyright (C) 2013 Cetsoft, http://www.cetsoft.com
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Library General Public
* License as published by the Free Software Foundation; either
* version 2 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Library General Public License for more details.
*
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free
* Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
* 
* Author : Yusuf Aytas
* Date   : May 22, 2014
*/
package com.cetsoft.imcache.bytebuffer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * The Class DirectByteBufferTest.
 */
public class OffHeapByteBufferStoreTest {
	
	/** The random. */
	Random random;
	
	/** The pointer. */
	@Mock
	Pointer pointer;
	
	/** The queue. */
	@Mock
	BlockingQueue<Integer> queue;

	/** The buffer. */
	@Mock
	OffHeapByteBuffer buffer;
	
	/** The buffer store. */
	@Spy
	OffHeapByteBufferStore bufferStore = new OffHeapByteBufferStore(1000, 1);
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		random = new Random();
	}
	
	/**
	 * Store.
	 */
	@Test
	public void store(){
		int size = 100;
		byte[] expectedBytes = new byte[size];
		random.nextBytes(expectedBytes);
		Pointer pointer = bufferStore.store(expectedBytes);
		byte[] actualBytes = bufferStore.retrieve(pointer);
		assertArrayEquals(expectedBytes, actualBytes);
	}
	
	/**
	 * Store buffer over flow.
	 */
	@Test
	public void storeBufferOverFlow(){
		int size = 100;
		byte[] expectedBytes = new byte[size];
		random.nextBytes(expectedBytes);
		doReturn(buffer).when(bufferStore).currentBuffer();
		doThrow(new BufferOverflowException()).doReturn(pointer).when(buffer).store(expectedBytes);
		Pointer actualPointer = bufferStore.store(expectedBytes);
		assertEquals(pointer, actualPointer);
		verify(bufferStore,times(2)).currentBuffer();
		verify(buffer,times(2)).store(expectedBytes);
	}
	
	/**
	 * Store buffer over flow next buffer.
	 */
	@Test
	public void storeBufferOverFlowNextBuffer(){
		int size = 100;
		byte[] expectedBytes = new byte[size];
		random.nextBytes(expectedBytes);
		doReturn(buffer).when(bufferStore).currentBuffer();
		doThrow(new BufferOverflowException()).doThrow(new BufferOverflowException()).
			doReturn(pointer).when(buffer).store(expectedBytes);
		Pointer actualPointer = bufferStore.store(expectedBytes);
		assertEquals(pointer, actualPointer);
		verify(bufferStore,times(3)).currentBuffer();
		verify(buffer,times(3)).store(expectedBytes);
	}
	
	/**
	 * Update.
	 */
	@Test
	public void update(){
		int size = 100;
		byte[] bytes = new byte[size];
		random.nextBytes(bytes);
		Pointer pointer = bufferStore.store(bytes);
		byte[] expectedBytes = new byte[size];
		random.nextBytes(expectedBytes);
		pointer = bufferStore.update(pointer, expectedBytes);
		byte[] actualBytes = bufferStore.retrieve(pointer);
		assertArrayEquals(expectedBytes, actualBytes);
	}
	
	/**
	 * Update buffer over flow.
	 */
	@Test
	public void updateBufferOverFlow(){
		int size = 100;
		byte[] bytes = new byte[size];
		random.nextBytes(bytes);
		byte[] expectedBytes = new byte[size];
		random.nextBytes(expectedBytes);
		doReturn(buffer).when(pointer).getOffHeapByteBuffer();
		doThrow(new BufferOverflowException()).when(buffer).update(pointer, expectedBytes);
		pointer = bufferStore.update(pointer, expectedBytes);
		verify(bufferStore).store(expectedBytes);
	}
	
	/**
	 * Next buffer buffer over flow.
	 */
	@Test(expected=BufferOverflowException.class)
	public void nextBufferBufferOverFlow(){
		bufferStore.availableBuffers = queue;
		doReturn(null).when(queue).poll();
		bufferStore.nextBuffer();
		verify(queue).poll();
	}
	
	/**
	 * Store with buffer.
	 */
	@Test
	public void storeWithBuffer(){
		int size = 100;
		byte[] bytes = new byte[size];
		random.nextBytes(bytes);
		doReturn(pointer).when(bufferStore).store(bytes);
		doReturn(buffer).doReturn(new OffHeapByteBuffer(0, 10)).when(bufferStore).currentBuffer();
		doNothing().when(bufferStore).nextBuffer();
		bufferStore.store(bytes, buffer);
		verify(bufferStore).store(bytes);
		verify(bufferStore).nextBuffer();
	}
	
	/**
	 * Dirty memory.
	 */
	@Test
	public void dirtyMemory(){
		long size = 100;
		bufferStore.buffers = new OffHeapByteBuffer[]{buffer};
		doReturn(size).when(buffer).dirtyMemory();
		long actualSize = bufferStore.dirtyMemory();
		assertEquals(size, actualSize);
	}
	
	/**
	 * Free memory.
	 */
	@Test
	public void freeMemory(){
		long size = 100;
		bufferStore.buffers = new OffHeapByteBuffer[]{buffer};
		doReturn(size).when(buffer).freeMemory();
		long actualSize = bufferStore.freeMemory();
		assertEquals(size, actualSize);
	}
	
	/**
	 * Used memory.
	 */
	@Test
	public void usedMemory(){
		long size = 100;
		bufferStore.buffers = new OffHeapByteBuffer[]{buffer};
		doReturn(size).when(buffer).usedMemory();
		long actualSize = bufferStore.usedMemory();
		assertEquals(size, actualSize);
	}
	
	/**
	 * Free.
	 */
	@Test
	public void free(){
		doNothing().when(bufferStore).free(anyInt());
		bufferStore.free();
		verify(bufferStore).free(anyInt());
	}
	
	/**
	 * Free with index.
	 */
	@Test
	public void freeWithIndex(){
		bufferStore.buffers = new OffHeapByteBuffer[]{buffer};
		bufferStore.availableBuffers = queue;
		doReturn(true).when(queue).add(anyInt());
		doNothing().when(buffer).free();
		bufferStore.free(0);
		verify(buffer).free();
		verify(queue).add(anyInt());
	}
	
	/**
	 * Pointers to be redistributed.
	 */
	@Test
	public void pointersToBeRedistributed(){
		int size = 100;
		byte[] expectedBytes = new byte[size];
		random.nextBytes(expectedBytes);
		List<Pointer> pointers = new ArrayList<Pointer>();
		pointers.add(pointer);
		doReturn(expectedBytes).when(bufferStore).retrieve(pointer);
		doReturn(buffer).when(pointer).getOffHeapByteBuffer();
		doReturn(pointer).when(bufferStore).store(expectedBytes, buffer);
		doReturn(pointer).when(pointer).copy(pointer);
		bufferStore.redistribute(pointers);
		verify(bufferStore).retrieve(pointer);
		verify(pointer).getOffHeapByteBuffer();
		verify(pointer).copy(pointer);
		verify(bufferStore).store(expectedBytes, buffer);
	}
	
	@Test
	public void remove(){
		int size = 100;
		byte[] expectedBytes = new byte[size];
		random.nextBytes(expectedBytes);
		doReturn(buffer).when(pointer).getOffHeapByteBuffer();
		doReturn(expectedBytes).when(buffer).remove(pointer);
		byte[] actualBytes = bufferStore.remove(pointer);
		assertEquals(expectedBytes, actualBytes);
	}
	
}

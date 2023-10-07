package com.imooc.utils;

import java.lang.reflect.Array;

public class SizedStack<T> {

	T[] array;
	int size;
	int index = -1;

	@SuppressWarnings("unchecked")
	public SizedStack(Class<T> clazz, int size) {
		this.array = (T[]) Array.newInstance(clazz, size);
		this.size = size;
	}

	public int size() {
		return this.size;
	}

	public int length() {
		return this.index + 1;
	}

	public void push(T t) {
		if (index + 1 < size)
			array[++index] = t;
		else {
			for (int i = 1; i < size; i++) {
				array[i - 1] = array[i];
			}
			array[size - 1] = t;
		}
	}

	public T pop() {
		if (index >= 0) {
			T t = array[index];
			array[index--] = null;
			return t;
		}
		return null;
	}

	public T first() {
		return array[0];
	}

	public T last() {
		if (index >= 0)
			return array[index--];
		return null;
	}

	public T get(int i) {
		return array[i];
	}

	public String toString() {
		String str = "[";
		for (int i = 0; i < size; i++) {
			if (!str.equals("["))
				str += ",";
			str += array[i];
		}
		str += "]";
		return str;
		//	return array.toString();
	}

	public static void main(String[] args) {
		SizedStack<Integer> stack = new SizedStack<Integer>(Integer.class, 4);
		stack.push(1);
		stack.push(2);
		stack.push(3);
		System.out.println(stack);
		stack.push(4);
		System.out.println(stack);
		stack.push(5);
		System.out.println(stack);
		stack.push(6);
		System.out.println(stack);
		System.out.println(stack.pop());
		System.out.println(stack);
	}

}

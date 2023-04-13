/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.domain.xmlns.evotingconfig;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

public class RegisterTypeWithVoterStream extends RegisterType {

	public RegisterTypeWithVoterStream(final Stream<VoterType> voters) {
		voter = new StreamList<>(voters);
	}

	class StreamList<T> implements List<T> {
		private final Stream<T> source;

		public StreamList(final Stream<T> source) {
			this.source = source;
		}

		@Override
		public int size() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEmpty() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<T> iterator() {
			return source.iterator();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final Collection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public T get(final int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public T set(final int index, final T element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final T element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public T remove(final int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int lastIndexOf(final Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator listIterator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator listIterator(final int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List subList(final int fromIndex, final int toIndex) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(final Collection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object[] toArray(final Object[] a) {
			throw new UnsupportedOperationException();
		}
	}
}

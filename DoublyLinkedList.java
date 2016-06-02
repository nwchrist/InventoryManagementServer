/**
 * DESCRIPTION: The List class implements a generic doubly linked list. The list
 * is a homogenous sequence of elements that grows & shrinks dynamically as
 * elements are added or removed.
 * 
 * The basic structure of a doubly linked list is a sequence of Nodes that
 * contain a value and a reference to both the previous and the next Node in the
 * list.
 * 
 * When a reference to the next Node is null it indicates the end of the list.
 * 
 * +----+ +----+ START -> |NODE| -> |NODE| -> null null <- | | <- | | <- END
 * +----+ +----+
 * 
 * CS 1122 Spring 2016
 * 
 * @param <V>
 *            - the parameterized data type of the values being stored as
 *            elements in the list.
 */
public class DoublyLinkedList<V> {
	/**
	 * DESCRIPTION: Nodes contain a value and a reference to the next Node in
	 * the list.
	 * 
	 * NOTE: Node does not need to be parameterized because it is defined as an
	 * inner class and can use the parameterized type from List. This type
	 * <V> is the data type of the value stored in the node.
	 * 
	 * Node is private because there is no need to share it.
	 */
	private class Node {
		/**
		 * The value stored as an element in the list.
		 */
		private V value = null;

		public V getValue() {
			return value;
		}

		public void setValue(V newval) {
			value = newval;
		}

		/**
		 * A reference to the next node in the list. Here a value of null
		 * indicates the end of the list.
		 */
		private Node next = null;

		public Node getNext() {
			return next;
		}

		public void setNext(Node newnode) {
			next = newnode;
		}

		/**
		 * A reference to the previous node in the list. Here a value of null
		 * indicates the end of the list.
		 */
		private Node prev = null;

		public Node getPrev() {
			return prev;
		}

		public void setPrev(Node newnode) {
			prev = newnode;
		}

		/**
		 * A constructor for a Node that allows us to set both the value and the
		 * next node reference at instantiation.
		 * 
		 * @param newval
		 * @param prevnode
		 * @param nextnode
		 */
		public Node(V newval, Node prevnode, Node nextnode) {
			value = newval;
			prev = prevnode;
			next = nextnode;
		}
	}

	/**
	 * The beginning of the list. If start is null, the list is empty. NOTE:
	 * Accessors are private because they are not part of List API.
	 */
	private Node start = null;

	private Node getStart() {
		return start;
	}

	private void setStart(Node newval) {
		start = newval;
	}

	/**
	 * The end of the list. If end is null, the list is empty. NOTE: Accessors
	 * are private because they are not part of the List API.
	 */
	private Node end = null;

	private Node getEnd() {
		return end;
	}

	private void setEnd(Node newval) {
		end = newval;
	}

	/**
	 * The size of the list is the number of Nodes in the list. NOTE: getSize( )
	 * is public, but setSize( ) is private We don't want users to change the
	 * size without adding/removing elements - that would be very bad.
	 */
	private int size = 0;

	public int getSize() {
		return size;
	}

	private void setSize(int newsize) {
		size = newsize;
	}

	/**
	 * @return true if the list is empty.
	 */
	public boolean isEmpty() {
		// Belt and suspenders
		return start == null || end == null || getSize() == 0;
	}

	public String toString() {
		String output = "[ ";
		Node current = start;
		while (current != null) {
			output += current.getValue() + " ";
			current = current.getNext();
		}
		output += "]";
		return output;
	}

	/**
	 * Adds a value in the list between positions (index-1) and index. + When
	 * index is 0, a new value is added to the start of the list. + When index
	 * is size, a new value is added to the end of the list.
	 * 
	 * @param index
	 *            : The position at which to insert a new value
	 * @param value
	 *            : The new value being added to the list.
	 * @throws IndexOutOfBoundsException
	 *             : Throw when the index is out of range.
	 */
	public void add(int index, V value) throws IndexOutOfBoundsException {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException();
		}
		Node current = start;
		int i = 0;
		while (i <= index && current != null) {
			current = current.next;
			i++;
		}
		Node newnode = new Node(value, current == null ? null : current.prev, current);
		if (current != null) {
			if (current.getPrev() != null) {
				current.prev.next = newnode;
			} else {
				start = newnode;
			}
		} else if (end != null) {
			newnode.setPrev(end);
			end.setNext(newnode);
			end = newnode;
		} else if (start != null) {
			end = newnode;
		} else {
			start = newnode;
			end = newnode;
		}
		size++;
	}

	/**
	 * A convenience method for adding to the end of the list.
	 * 
	 * @param value
	 *            : The new value being added to the list.
	 * @throws EmptyListException
	 *             : Throw when the list is empty
	 * @throws IndexOutOfBoundsException
	 *             : Throw when the index is out of range.
	 */
	public void add(V value) throws EmptyListException, IndexOutOfBoundsException {
		add(size, value);
	}

	/**
	 * A convenience method for getting the first value in the list
	 * 
	 * @return The value of the first node in the list.
	 * @throws EmptyListException
	 *             : Throw when the list is empty
	 */
	public V first() throws EmptyListException {
		if (isEmpty()) {
			throw new EmptyListException();
		}
		return start.getValue();
	}

	/**
	 * Searches through the list for the Node at the specified index and if
	 * found, returns that Node's value.
	 * 
	 * @param index
	 *            : The position at which to insert a new value
	 * @return The value of the Node at the specified index.
	 * @throws EmptyListException
	 *             : Throw when the list is empty
	 * @throws IndexOutOfBoundsException
	 *             : Throw when the index is out of range.
	 */
	public V get(int index) throws EmptyListException, IndexOutOfBoundsException {
		if (isEmpty()) {
			throw new EmptyListException();
		}
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException();
		}
		Node current = start;
		for (int i = 0; i < size; i++) {
			if (i == index) {
				break;
			}
			current = current.next;
		}
		return current.getValue();
	}

	/**
	 * Removes the Node at the specified index and returns its value.
	 * 
	 * @param index
	 *            : The position of the Node to be removed from the list.
	 * @return : The value of the Node being removed.
	 * @throws EmptyListException
	 *             : Thrown if the list is empty.
	 * @throws IndexOutOfBoundsException
	 *             : Thrown if index < 0 or index >= size.
	 */
	public V remove(int index) throws EmptyListException, IndexOutOfBoundsException {
		if (isEmpty()) {
			throw new EmptyListException();
		}
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException();
		}
		Node current = start;
		for (int i = 0; i < size; i++) {
			if (i == index) {
				break;
			}
			current = current.next;
		}
		if (current.prev != null) {
			current.prev.next = current.next;
		} else {
			start = current.next;
		}
		if (current.next != null) {
			current.next.prev = current.prev;
		} else {
			end = current.prev;
		}
		size--;
		return current.getValue();
	}

	/**
	 * Remove the first element in the list
	 * 
	 * @return : The value of the Node being removed.
	 * @throws EmptyListException
	 *             : Thrown if the list is empty.
	 */
	public V remove() throws EmptyListException {
		if (isEmpty()) {
			throw new EmptyListException();
		}
		V result = start.getValue();
		start = start.next;
		return result;
	}
}
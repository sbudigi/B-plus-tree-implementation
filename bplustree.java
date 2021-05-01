// Sreelalitha Budigi
// UF ID 36717336
import java.util.*;
import java.io.*;
import java.lang.*;
public class bplustree {
	int order_m;
	InternalNodeClass root_node;
	LeafNodeClass first_leaf_node;
	
	/**
	 * To search starting from the root to the leaf node that contains the 'key' within its dictionary by comparing keys.
	 * @param key: leaf node's key 
	 * @return the matching leaf node
	 */
	private LeafNodeClass searchForLeafNode(int key) 
	{
		// Initialize the key_collection and index variable
		Integer[] keys_collection = this.root_node.keys_collection;
		int index = 0;
		// Traverse through the nodes
		while(index < this.root_node.degree_value - 1) 
		{
			if (keys_collection[index] > key) 
			{ 
				break; 
			}
			index++;
		}
		// Return if matching node found else repeat the search 
		NodeClass child_node = this.root_node.pointers_for_child_nodes[index];
		if (child_node instanceof LeafNodeClass) 
		{
			return (LeafNodeClass)child_node;
		} 
		else 
		{
			return searchForLeafNode((InternalNodeClass)child_node, key);
		}
	}
	private LeafNodeClass searchForLeafNode(InternalNodeClass node, int key) 
	{
		// Initialize keys_collection and index variable
		Integer[] keys_collection = node.keys_collection;
		int index = 0;
		// Traverse through the nodes
		while(index < node.degree_value - 1) 
		{
			if (keys_collection[index] > key) 
			{ 
				break; 
			}
			index++;
		}
		// Return if matching node found else repeat the search
		NodeClass child_node = node.pointers_for_child_nodes[index];
		if (child_node instanceof LeafNodeClass) 
		{
			return (LeafNodeClass)child_node;
		} 
		else 
		{
			return searchForLeafNode((InternalNodeClass)node.pointers_for_child_nodes[index], key);
		}
	}

	/**
	 * To perform Binary Search and return the index of the dictionary pair that matches target else return -1.
	 * @param sorted_dictionary_pairs: sorted dictionary pair list 
	 * @param target_key_value: target 
	 * @return output index of the search
	 */
	private int binarySearch(DictionaryPairClass[] sorted_dictionary_pairs, int number_of_pairs, int target_key_value) 
	{
		Comparator<DictionaryPairClass> compare_pairs = new Comparator<DictionaryPairClass>() 
		{
			@Override
			public int compare(DictionaryPairClass ordered_pairs_1, DictionaryPairClass ordered_pairs_2) 
			{
				Integer a = Integer.valueOf(ordered_pairs_1.key);
				Integer b = Integer.valueOf(ordered_pairs_2.key);
				return a.compareTo(b);
			}
		};
		return Arrays.binarySearch(sorted_dictionary_pairs, 0, number_of_pairs, new DictionaryPairClass(target_key_value, 0), compare_pairs);
	}
	
	/**
	 * To return the lower bound of the maximum degree m of the tree.
	 * @return lower bound
	 */
	private int findCenterPoint() {
		return (int)Math.ceil((this.order_m + 1) / 2.0) - 1;
	}

	/**
	 * To return the pointer index from a list of pointers that points to the specified node.
	 * @param pointer_list: pointer list
	 * @param node: pointer to specific node
	 * @return pointer index 
	 */
	private int findPointerIndex(NodeClass[] pointer_list, LeafNodeClass node) 
	{
		int index = 0;
		while(index < pointer_list.length) 
		{
			if (node == pointer_list[index]) 
			{ 
				break; 
			}
			index++;
		}
		return index;
	}
	/**
	 * To correct the deficiency of the given node by performing borrow/merge.
	 * @param internal_node: internal node
	 */
	private void deficiencyHandling(InternalNodeClass internal_node) 
	{
		InternalNodeClass sibling_node;
		InternalNodeClass parent_node = internal_node.parent_node;
		int index = 0;
		// correct deficient node
		if (internal_node == this.root_node) 
		{
			while(index < internal_node.pointers_for_child_nodes.length) 
			{
				if (internal_node.pointers_for_child_nodes[index] != null) 
				{
					if (internal_node.pointers_for_child_nodes[index] instanceof InternalNodeClass) 
					{
						this.root_node = (InternalNodeClass)internal_node.pointers_for_child_nodes[index];
						this.root_node.parent_node = null;
					} 
					else if (internal_node.pointers_for_child_nodes[index] instanceof LeafNodeClass) 
					{
						this.root_node = null;
					}
				}
				index++;
			}
			
		}
		// Borrow
		else if (internal_node.leftside_sibling != null && internal_node.leftside_sibling.checkIfLendable()) 
		{
			sibling_node = internal_node.leftside_sibling;
		} 
		else if (internal_node.rightside_ibling != null && internal_node.rightside_ibling.checkIfLendable()) 
		{
			sibling_node = internal_node.rightside_ibling;
			// Copy one key-pointer pair from sibling
			int borrow_key = sibling_node.keys_collection[0];
			NodeClass pointer = sibling_node.pointers_for_child_nodes[0];
			// Copy root key-pointer pair into parent
			internal_node.keys_collection[internal_node.degree_value - 1] = parent_node.keys_collection[0];
			internal_node.pointers_for_child_nodes[internal_node.degree_value] = pointer;
			// Copy borrow key into root
			parent_node.keys_collection[0] = borrow_key;
			// Delete key-pointer pair from sibling
			sibling_node.removingThePointer(0);
			Arrays.sort(sibling_node.keys_collection);
			sibling_node.removingThePointer(0);
			shiftPointersDown(internal_node.pointers_for_child_nodes, 1);
		}
		// Merge
		else if (internal_node.leftside_sibling != null && internal_node.leftside_sibling.checkIfMergeable()) 
		{
		} 
		else if (internal_node.rightside_ibling != null && internal_node.rightside_ibling.checkIfMergeable()) 
		{
			sibling_node = internal_node.rightside_ibling;
			// Copy parent rightmost key to the front of sibling key and delete it
			sibling_node.keys_collection[sibling_node.degree_value - 1] = parent_node.keys_collection[parent_node.degree_value - 2];
			Arrays.sort(sibling_node.keys_collection, 0, sibling_node.degree_value);
			parent_node.keys_collection[parent_node.degree_value - 2] = null;
			// Copy given node's child pointer to sibling's child pointers list
			index = 0;
			while (index < internal_node.pointers_for_child_nodes.length) 
			{
				if (internal_node.pointers_for_child_nodes[index] != null) 
				{
					sibling_node.preAppendToChildPointer(internal_node.pointers_for_child_nodes[index]);
					internal_node.pointers_for_child_nodes[index].parent_node = sibling_node;
					internal_node.removingThePointer(index);
				}
				index++;
			}
			
			// Delete child pointer from its ancestor to deficient node
			parent_node.removingThePointer(internal_node);

			// Remove left sibling
			sibling_node.leftside_sibling = internal_node.leftside_sibling;
		}
		// Handle deficiency in next level if exists
		if (parent_node != null && parent_node.checkIfDeficient()) 
		{
			deficiencyHandling(parent_node);
		}
	}
	
	/**
	 * To perform linear search on the dictionary and return the index of the first null entry if exists else -1.
	 * @param pointer_list: pointer list
	 * @return search result index
	 */
	private int linearSearchForNull(NodeClass[] pointer_list) 
	{
		int index = 0;
		while ( index <  pointer_list.length) 
		{
			if (pointer_list[index] == null) 
			{ 
				return index; 
			}
			index++;
		} 
		
		return -1;
	}
	/**
	 * To check if the tree is empty or not.
	 * @return boolean value
	 */
	private boolean checkIfEmpty() 
	{
		return first_leaf_node == null;
	}
	/**
	 * To perform linear search on the dictionary and return the index of the first null entry if exists else -1. 
	 * @param sorted_dictionary_pairs: dictionary list of nodes
	 * @return search result index
	 */
	private int linearSearchForNull(DictionaryPairClass[] sorted_dictionary_pairs) 
	{
		int index = 0;
		while ( index <  sorted_dictionary_pairs.length) 
		{
			if (sorted_dictionary_pairs[index] == null) 
			{ 
				return index; 
			}
			index++;
		}
		
		return -1;
	}
	/**
	 * To shift down a set of pointers that are pre-appended by null values.
	 * @param pointer_list: pointer list
	 * @param amount_of_shift: amount of shift
	 */
	private void shiftPointersDown(NodeClass[] pointer_list, int amount_of_shift) 
	{
		NodeClass[] new_pointers_list = new NodeClass[this.order_m + 1];
		int index = amount_of_shift;
		while ( index < pointer_list.length) 
		{
			new_pointers_list[index - amount_of_shift] = pointer_list[index];
			index++;
		} 
		
		pointer_list = new_pointers_list;
	}
	
	/**
	 * To modify the internal nodes by removing all child pointers after the specified split and return the removed pointers. 
	 * @param internal_node: internal nodes
	 * @param index_for_split: split index
	 * @return removed pointer list
	 */
	private NodeClass[] dividingChildPointers(InternalNodeClass internal_node, int index_for_split) 
	{
		NodeClass[] pointer_list = internal_node.pointers_for_child_nodes;
		NodeClass[] copy_half_of_pointers = new NodeClass[this.order_m + 1];
		// Copy one half of values while updating the keys
		int index = index_for_split + 1;
		while ( index < pointer_list.length) 
		{
			copy_half_of_pointers[index - index_for_split - 1] = pointer_list[index];
			internal_node.removingThePointer(index);
			index++;
		}
		
		return copy_half_of_pointers;
	}
	/**
	 * To sort dictionary list incase if they have scattered null values.
	 * @param dictionary_list: dictionary list
	 */
	private void sortDictionaryList(DictionaryPairClass[] dictionary_list) 
	{
		Arrays.sort(dictionary_list, new Comparator<DictionaryPairClass>() 
		{
			@Override
			public int compare(DictionaryPairClass ordered_pairs_1, DictionaryPairClass ordered_pairs_2) 
			{
				if (ordered_pairs_1 == null && ordered_pairs_2 == null) 
				{ 
					return 0; 
				}
				if (ordered_pairs_1 == null) 
				{ 
					return 1; 
				}
				if (ordered_pairs_2 == null) 
				{ 
					return -1; 
				}
				return ordered_pairs_1.compareTo(ordered_pairs_2);
			}
		});
	}
	/**
	 * Split a single dictionary into two equal length ones.
	 * @param list_dictionary: dictionary
	 * @param index_for_split: index
	 * @return two dictionaries
	 */
	private DictionaryPairClass[] splitDictionaryList(LeafNodeClass list_dictionary, int index_for_split) 
	{
		DictionaryPairClass[] dictionary_list = list_dictionary.dictionary_list;
		/* Initialize two dictionaries */
		DictionaryPairClass[] copy_half_of_dict = new DictionaryPairClass[this.order_m];
		// Copy half of the values to each dictionary
		int index = index_for_split;
		while ( index < dictionary_list.length) 
		{
			copy_half_of_dict[index - index_for_split] = dictionary_list[index];
			list_dictionary.delete(index);
			index++;
		}
		
		return copy_half_of_dict;
	}
	
	/**
	 * Used when splitting nodes.
	 * @param keys_collection: keys
	 * @param index_for_split: split index
	 * @return removed keys
	 */
	private Integer[] dividingKeys(Integer[] keys_collection, int index_for_split) 
	{
		Integer[] dividing_of_keys = new Integer[this.order_m];
		// Remove split index value from keys
		keys_collection[index_for_split] = null;
		int index = index_for_split + 1;
		while ( index < keys_collection.length) 
		{
			dividing_of_keys[index - index_for_split - 1] = keys_collection[index];
			keys_collection[index] = null;
			index++;
		}
		
		return dividing_of_keys;
	}
	/**
	 * Remove the given key from the tree.
	 * @param key: key 
	 */
	public void delete(int key) 
	{
		if (checkIfEmpty()) 
		{
			/* If tree empty */
			System.err.println("The tree is empty.");
		} 
		else 
		{
			// Find index of key to delete
			LeafNodeClass list_dictionary = (this.root_node == null) ? this.first_leaf_node : searchForLeafNode(key);
			int dict_pair_index = binarySearch(list_dictionary.dictionary_list, list_dictionary.number_of_pairs, key);
			if (dict_pair_index < 0) 
			{
				/* When key not found */
				System.err.println("Key not found.");
			} 
			else 
			{
				list_dictionary.delete(dict_pair_index);
				// Check deficiency
				if (list_dictionary.checkIfDeficient()) 
				{
					LeafNodeClass sibling_node;
					InternalNodeClass parent_node = list_dictionary.parent_node;
					// Borrow
					if (list_dictionary.leftside_sibling != null &&
						list_dictionary.leftside_sibling.parent_node == list_dictionary.parent_node &&
						list_dictionary.leftside_sibling.checkIfLendable()) 
						{
						sibling_node = list_dictionary.leftside_sibling;
						DictionaryPairClass borrowed_dict_pair = sibling_node.dictionary_list[sibling_node.number_of_pairs - 1];
						/* Insert borrowed pair, sort and delete the pair */
						list_dictionary.insert(borrowed_dict_pair);
						sortDictionaryList(list_dictionary.dictionary_list);
						sibling_node.delete(sibling_node.number_of_pairs - 1);
						// Update key
						int index_of_pointer = findPointerIndex(parent_node.pointers_for_child_nodes, list_dictionary);
						if (!(borrowed_dict_pair.key >= parent_node.keys_collection[index_of_pointer - 1])) 
						{
							parent_node.keys_collection[index_of_pointer - 1] = list_dictionary.dictionary_list[0].key;
						}
					} 
					else if (list_dictionary.rightside_ibling != null &&
							   list_dictionary.rightside_ibling.parent_node == list_dictionary.parent_node &&
							   list_dictionary.rightside_ibling.checkIfLendable()) 
							   {
						sibling_node = list_dictionary.rightside_ibling;
						DictionaryPairClass borrowed_dict_pair = sibling_node.dictionary_list[0];
						/* Insert borrowed pair, sort and delete the pair */
						list_dictionary.insert(borrowed_dict_pair);
						sibling_node.delete(0);
						sortDictionaryList(sibling_node.dictionary_list);
						// Update key
						int index_of_pointer = findPointerIndex(parent_node.pointers_for_child_nodes, list_dictionary);
						if (!(borrowed_dict_pair.key < parent_node.keys_collection[index_of_pointer])) 
						{
							parent_node.keys_collection[index_of_pointer] = sibling_node.dictionary_list[0].key;
						}
					}
					// Merge
					else if (list_dictionary.leftside_sibling != null &&
							 list_dictionary.leftside_sibling.parent_node == list_dictionary.parent_node &&
							 list_dictionary.leftside_sibling.checkIfMergeable()) 
							 {
						sibling_node = list_dictionary.leftside_sibling;
						int index_of_pointer = findPointerIndex(parent_node.pointers_for_child_nodes, list_dictionary);
						// Remove key and pointer from parent
						parent_node.removingTheKey(index_of_pointer - 1);
						parent_node.removingThePointer(list_dictionary);
						// Update sibling 
						sibling_node.rightside_ibling = list_dictionary.rightside_ibling;
						// Check for deficiency 
						if (parent_node.checkIfDeficient()) 
						{
							deficiencyHandling(parent_node);
						}
					} 
					else if (list_dictionary.rightside_ibling != null &&
							   list_dictionary.rightside_ibling.parent_node == list_dictionary.parent_node &&
							   list_dictionary.rightside_ibling.checkIfMergeable()) 
							   {
						sibling_node = list_dictionary.rightside_ibling;
						int index_of_pointer = findPointerIndex(parent_node.pointers_for_child_nodes, list_dictionary);
						// Remove key from parent
						parent_node.removingTheKey(index_of_pointer);
						parent_node.removingThePointer(index_of_pointer);
						// Update sibling
						sibling_node.leftside_sibling = list_dictionary.leftside_sibling;
						if (sibling_node.leftside_sibling == null) 
						{
							first_leaf_node = sibling_node;
						}
						if (parent_node.checkIfDeficient()) 
						{
							deficiencyHandling(parent_node);
						}
					}

				} 
				else if (this.root_node == null && this.first_leaf_node.number_of_pairs == 0) 
				{
					/* When the only pair within the tree is deleted */
					// Set first leaf null to indicate tree is empty
					this.first_leaf_node = null;
				} 
				else 
				{
					sortDictionaryList(list_dictionary.dictionary_list);
				}
			}
		}
	}
	/**
	 * To handle an insertion that causes an overfull node.
	 * @param internal_node: overfull node
	 */
	private void dividingInternalNode(InternalNodeClass internal_node) 
	{
		// Parent_node
		InternalNodeClass parent_node = internal_node.parent_node;
		// Split to half
		int center_point = findCenterPoint();
		int new_parent_key = internal_node.keys_collection[center_point];
		Integer[] dividing_of_keys = dividingKeys(internal_node.keys_collection, center_point);
		NodeClass[] copy_half_of_pointers = dividingChildPointers(internal_node, center_point);
		// Change degree
		internal_node.degree_value = linearSearchForNull(internal_node.pointers_for_child_nodes);
		// Create new sibling to add half of the dictionary
		InternalNodeClass sibling_node = new InternalNodeClass(this.order_m, dividing_of_keys, copy_half_of_pointers);
		for (NodeClass pointer : copy_half_of_pointers) 
		{
			if (pointer != null) 
			{ 
				pointer.parent_node = sibling_node; 
			}
		}
		// Make siblings
		sibling_node.rightside_ibling = internal_node.rightside_ibling;
		if (sibling_node.rightside_ibling != null) 
		{
			sibling_node.rightside_ibling.leftside_sibling = sibling_node;
		}
		internal_node.rightside_ibling = sibling_node;
		sibling_node.leftside_sibling = internal_node;
		if (parent_node == null) 
		{
			// Create new root
			Integer[] keys_collection = new Integer[this.order_m];
			keys_collection[0] = new_parent_key;
			InternalNodeClass new_root_node = new InternalNodeClass(this.order_m, keys_collection);
			new_root_node.appendingToChildPointer(internal_node);
			new_root_node.appendingToChildPointer(sibling_node);
			this.root_node = new_root_node;
			internal_node.parent_node = new_root_node;
			sibling_node.parent_node = new_root_node;
		} 
		else 
		{
			// Add key to parent
			parent_node.keys_collection[parent_node.degree_value - 1] = new_parent_key;
			Arrays.sort(parent_node.keys_collection, 0, parent_node.degree_value);
			// Point new sibling
			int index_of_pointer = parent_node.findPointerIndex(internal_node) + 1;
			parent_node.insertionWithinChildPointer(sibling_node, index_of_pointer);
			sibling_node.parent_node = parent_node;
		}
	}
	/**
	 * Insert the given key-value.
	 * @param key: key
	 * @param value: value
	 */
	public void insert(int key, double value)
	{
		if (checkIfEmpty()) 
		{
			/* For first insert only */
			// Create first leaf node
			LeafNodeClass list_dictionary = new LeafNodeClass(this.order_m, new DictionaryPairClass(key, value));
			this.first_leaf_node = list_dictionary;
		} 
		else 
		{
			// Find leaf node to insert
			LeafNodeClass list_dictionary = (this.root_node == null) ? this.first_leaf_node : searchForLeafNode(key);
			// If node becomes overfull
			if (!list_dictionary.insert(new DictionaryPairClass(key, value))) 
			{
				// Sort the pair to be inserted
				list_dictionary.dictionary_list[list_dictionary.number_of_pairs] = new DictionaryPairClass(key, value);
				list_dictionary.number_of_pairs++;
				sortDictionaryList(list_dictionary.dictionary_list);
				// Divide the sorted pairs
				int center_point = findCenterPoint();
				DictionaryPairClass[] copy_half_of_dict = splitDictionaryList(list_dictionary, center_point);
				if (list_dictionary.parent_node == null) 
				{
					/* Already one node present */
					// Create node to serve as parent
					Integer[] parent_keys = new Integer[this.order_m];
					parent_keys[0] = copy_half_of_dict[0].key;
					InternalNodeClass parent_node = new InternalNodeClass(this.order_m, parent_keys);
					list_dictionary.parent_node = parent_node;
					parent_node.appendingToChildPointer(list_dictionary);
				} 
				else 
				{
					/* Parent exists */
					// Add new key for proper indexing
					int new_parent_key = copy_half_of_dict[0].key;
					list_dictionary.parent_node.keys_collection[list_dictionary.parent_node.degree_value - 1] = new_parent_key;
					Arrays.sort(list_dictionary.parent_node.keys_collection, 0, list_dictionary.parent_node.degree_value);
				}
				// To hold the other half
				LeafNodeClass new_leaf_node = new LeafNodeClass(this.order_m, copy_half_of_dict, list_dictionary.parent_node);
				int index_of_pointer = list_dictionary.parent_node.findPointerIndex(list_dictionary) + 1;
				list_dictionary.parent_node.insertionWithinChildPointer(new_leaf_node, index_of_pointer);
				// Make leaf nodes as siblings
				new_leaf_node.rightside_ibling = list_dictionary.rightside_ibling;
				if (new_leaf_node.rightside_ibling != null) 
				{
					new_leaf_node.rightside_ibling.leftside_sibling = new_leaf_node;
				}
				list_dictionary.rightside_ibling = new_leaf_node;
				new_leaf_node.leftside_sibling = list_dictionary;
				if (this.root_node == null) 
				{
					// Make root as parent
					this.root_node = list_dictionary.parent_node;

				} 
				else 
				{
					/* If parent is full we repeat till no deficiencies are found */
					InternalNodeClass internal_node = list_dictionary.parent_node;
					while (internal_node != null) 
					{
						if (internal_node.checkIfOverfull()) 
						{
							dividingInternalNode(internal_node);
						} 
						else 
						{
							break;
						}
						internal_node = internal_node.parent_node;
					}
				}
			}
		}
	}
	
	/**
	 * To traverse the tree.
	 * @param lower_bound_range: lower bound of the range
	 * @param upper_bound_range: upper bound of the range
	 * @return a list of pairs within the specified range
	 */
	public ArrayList<Double> search(int lower_bound_range, int upper_bound_range) 
	{
		ArrayList<Double> values = new ArrayList<Double>();
		LeafNodeClass current_node = this.first_leaf_node;
		while (current_node != null) 
		{
			DictionaryPairClass sorted_dictionary_pairs[] = current_node.dictionary_list;
			for (DictionaryPairClass dict_pair : sorted_dictionary_pairs) 
			{
				/* Stop searching if null value is encountered */
				if (dict_pair == null) { break; }
				// Include value if within the range
				if (lower_bound_range <= dict_pair.key && dict_pair.key <= upper_bound_range) 
				{
					values.add(dict_pair.value);
				}
			}
			/* Update current node to right sibling node and leaf traversal is from left to right */
			current_node = current_node.rightside_ibling;
		}
		return values;
	}
	/**
	 * Return the value associated with the key in the tree.
	 * @param key: key
	 * @return value
	 */
	public Double search(int key) 
	{
		// If empty return null
		if (checkIfEmpty()) 
		{ 
			return null; 
		}
		// Find leaf node for the key
		LeafNodeClass list_dictionary = (this.root_node == null) ? this.first_leaf_node : searchForLeafNode(key);
		// Perform binary search to find index
		DictionaryPairClass[] sorted_dictionary_pairs = list_dictionary.dictionary_list;
		int index = binarySearch(sorted_dictionary_pairs, list_dictionary.number_of_pairs, key);
		// If index negative return null
		if (index < 0) 
		{
			return null;
		} 
		else 
		{
			return sorted_dictionary_pairs[index].value;
		}
	}
	/**
	 * Constructor
	 * @param order_m: the order of the tree
	 */
	public bplustree(int order_m) 
	{
		this.order_m = order_m;
		this.root_node = null;
	}
	/**
	 * To represent a general node within the tree.
	 */
	public class NodeClass 
	{
		InternalNodeClass parent_node;
	}
	/**
	 * To represent the internal nodes within the tree.
	 */
	private class InternalNodeClass extends NodeClass 
	{
		int maximum_degree;
		int minimum_degree;
		int degree_value;
		InternalNodeClass leftside_sibling;
		InternalNodeClass rightside_ibling;
		Integer[] keys_collection;
		NodeClass[] pointers_for_child_nodes;
		/**
		 * To append given pointer to the end of node.
		 * @param pointer: pointer
		 */
		private void appendingToChildPointer(NodeClass pointer) 
		{
			this.pointers_for_child_nodes[degree_value] = pointer;
			this.degree_value++;
		}
		
		/**
		 * To insert the pointer at the specified index.
		 * @param pointer: pointer to insert
		 * @param index_value: index 
		 */
		private void insertionWithinChildPointer(NodeClass pointer, int index_value) 
		{
			int index = degree_value - 1;
			while ( index >= index_value ) 
			{
				pointers_for_child_nodes[index + 1] = pointers_for_child_nodes[index];
				index--;
			}
			
			this.pointers_for_child_nodes[index_value] = pointer;
			this.degree_value++;
		}
		/**
		 * To find the index of given pointer.
		 * @param pointer:  pointer
		 * @return index
		 */
		private int findPointerIndex(NodeClass pointer) 
		{
			int index = 0;
			while ( index < pointers_for_child_nodes.length) 
			{
				if (pointers_for_child_nodes[index] == pointer) 
				{ 
					return index; 
				}
				index++;
			}
			return -1;
		}
		/**
		 * To check if nodes are deficient.
		 * @return boolean value
		 */
		private boolean checkIfDeficient() {
			return this.degree_value < this.minimum_degree;
		}
		
		/**
		 * To check if the nodes are full.
		 * @return boolean value
		 */
		private boolean checkIfOverfull() 
		{
			return this.degree_value == maximum_degree + 1;
		}
		/**
		 * To insert the given pointer to the start.
		 * @param pointer: pointer to pre append
		 */
		private void preAppendToChildPointer(NodeClass pointer) 
		{
			for (int index = degree_value - 1; index >= 0 ;index--) 
			{
				pointers_for_child_nodes[index + 1] = pointers_for_child_nodes[index];
			}
			this.pointers_for_child_nodes[0] = pointer;
			this.degree_value++;
		}
		/**
		 * To check if nodes are lendable.
		 * @return boolean value.
		 */
		private boolean checkIfLendable() 
		{ 
			return this.degree_value > this.minimum_degree; 
		}
		/**
		 * To check if nodes are mergeable.
		 * @return boolean value
		 */
		private boolean checkIfMergeable() 
		{ 
			return this.degree_value == this.minimum_degree; 
		}
		/**
		 * To set a key to null.
		 * @param index: key to be set null
		 */
		private void removingTheKey(int index) 
		{ 
			this.keys_collection[index] = null; 
		}
		/**
		 * To set a pointer to null.
		 * @param index: pointer to be set null
		 */
		private void removingThePointer(int index) 
		{
			this.pointers_for_child_nodes[index] = null;
			this.degree_value--;
		}
		
		/**
		 * Constructor
		 * @param order_m: max degree
		 * @param keys_collection: list of keys
		 */
		private InternalNodeClass(int order_m, Integer[] keys_collection) 
		{
			this.maximum_degree = order_m;
			this.minimum_degree = (int)Math.ceil(order_m/2.0);
			this.degree_value = 0;
			this.keys_collection = keys_collection;
			this.pointers_for_child_nodes = new NodeClass[this.maximum_degree+1];
		}
		/**
		 * To remove a pointer.
		 * @param pointer: pointer to be removed 
		 */
		private void removingThePointer(NodeClass pointer) 
		{
			int index = 0;
			while ( index < pointers_for_child_nodes.length) 
			{
				if (pointers_for_child_nodes[index] == pointer) 
				{ 
					this.pointers_for_child_nodes[index] = null; 
				}
				index++;
			}
			
			this.degree_value--;
		}
		/**
		 * Constructor
		 * @param order_m: max degree
		 * @param keys_collection: list of keys
		 * @param pointer_list: list of pointers
		 */
		private InternalNodeClass(int order_m, Integer[] keys_collection, NodeClass[] pointer_list) 
		{
			this.maximum_degree = order_m;
			this.minimum_degree = (int)Math.ceil(order_m/2.0);
			this.degree_value = linearSearchForNull(pointer_list);
			this.keys_collection = keys_collection;
			this.pointers_for_child_nodes = pointer_list;
		}
	}
	/**
	 * To represent the leaf nodes within the tree using a doubly linked list
	*/
	public class LeafNodeClass extends NodeClass 
	{
		int maximum_number_of_pairs;
		int minimum_number_of_pairs;
		int number_of_pairs;
		LeafNodeClass leftside_sibling;
		LeafNodeClass rightside_ibling;
		DictionaryPairClass[] dictionary_list;
		
		/**
		 * To insert a key value pair within the dictionary list.
		 * @param dict_pair: pair to insert
		 * @return boolean value
		 */
		public boolean insert(DictionaryPairClass dict_pair) 
		{
			if (this.checkIfFull()) 
			{
				return false;
			} 
			else 
			{
				// Insert pair, increment counter, sort list
				this.dictionary_list[number_of_pairs] = dict_pair;
				number_of_pairs++;
				Arrays.sort(this.dictionary_list, 0, number_of_pairs);
				return true;
			}
		}
		/**
		 * Delete a given index.
		 * @param index: index to delete
		 */
		public void delete(int index) 
		{
			this.dictionary_list[index] = null;
			number_of_pairs--;
		}
		/**
		 * To check if a node is deficient.
		 * @return boolean value
		 */
		public boolean checkIfDeficient() 
		{ 
			return number_of_pairs < minimum_number_of_pairs; 
		}
		
		/**
		 * To check if a node can lend to a deficient leaf node.
		 * @return boolean value
		 */
		public boolean checkIfLendable() 
		{ 
			return number_of_pairs > minimum_number_of_pairs; 
		}
		/**
		 * To check if a node can be merged.
		 * @return boolean value
		 */
		public boolean checkIfMergeable() 
		{
			return number_of_pairs == minimum_number_of_pairs;
		}
		/**
		 * To check if a node is full.
		 * @return boolean value
		 */
		public boolean checkIfFull() 
		{ 
			return number_of_pairs == maximum_number_of_pairs; 
		}
		/**
		 * Constructor
		 * @param order_m: order of the tree
		 * @param dict_pair: dictionary list
		 */
		public LeafNodeClass(int order_m, DictionaryPairClass dict_pair) 
		{
			this.maximum_number_of_pairs = order_m - 1;
			this.minimum_number_of_pairs = (int)(Math.ceil(order_m/2) - 1);
			this.dictionary_list = new DictionaryPairClass[order_m];
			this.number_of_pairs = 0;
			this.insert(dict_pair);
		}
		/**
		 * Constructor
		 * @param sorted_dictionary_pairs: dictionar list
		 * @param order_m: order of the tree
		 * @param parent_node: parent node
		 */
		public LeafNodeClass(int order_m, DictionaryPairClass[] sorted_dictionary_pairs, InternalNodeClass parent_node) 
		{
			this.maximum_number_of_pairs = order_m - 1;
			this.minimum_number_of_pairs = (int)(Math.ceil(order_m/2) - 1);
			this.dictionary_list = sorted_dictionary_pairs;
			this.number_of_pairs = linearSearchForNull(sorted_dictionary_pairs);
			this.parent_node = parent_node;
		}
	}
	/**
	 * Represent dictionary list within the leaf nodes of the tree. 
	 */
	public class DictionaryPairClass implements Comparable<DictionaryPairClass> 
	{
		int key;
		double value;
		/**
		 * Constructor
		 * @param key: key
		 * @param value: value
		 */
		public DictionaryPairClass(int key, double value) 
		{
			this.key = key;
			this.value = value;
		}
		/**
		 * To perform comparisons
		 * @param object
		 * @return
		 */
		@Override
		public int compareTo(DictionaryPairClass object) 
		{
			if (key == object.key) 
			{ 
				return 0; 
			}
			else if (key > object.key) 
			{ 
				return 1; 
			}
			else 
			{ 
				return -1; 
			}
		}
	}
	public static void main(String[] args) 
	{
		if (args.length != 1) 
		{
			System.err.println("Provide a input file");
			System.exit(-1);
		}
		String name_of_file = args[0];
		try 
		{
			// Read input file
			File file_pointer = new File(System.getProperty("user.dir") + "/" + name_of_file);
			Scanner input_scanner = new Scanner(file_pointer);
			// Create output file to store results
			FileWriter generate_output = new FileWriter("output_file.txt", false);
			boolean line_first = true;
			bplustree bplustree_object = null;
			while (input_scanner.hasNextLine()) 
			{
				String current_line = input_scanner.nextLine().replace(" ", "");
				String[] get_tokens = current_line.split("[(,)]");
				switch (get_tokens[0]) 
				{
					// Initialize
					case "Initialize":
						bplustree_object = new bplustree(Integer.parseInt(get_tokens[1]));
						break;
					// Insert into the tree
					case "Insert":
						bplustree_object.insert(Integer.parseInt(get_tokens[1]), Double.parseDouble(get_tokens[2]));
						break;
					// Delete from the tree
					case "Delete":
						bplustree_object.delete(Integer.parseInt(get_tokens[1]));
						break;
					case "Search":
						String search_result = "";
						// Perform search across a range
						if (get_tokens.length == 3) 
						{
							ArrayList<Double> values = bplustree_object.search(Integer.parseInt(get_tokens[1]), Integer.parseInt(get_tokens[2]));
							if (values.size() != 0) 
							{
								for (double v : values) 
								{ 
									search_result += v + ", "; 
								}
								search_result = search_result.substring(0, search_result.length() - 2);
							} 
							else 
							{
								search_result = "Null";
							}
						}
						// Perform search operation
						else 
						{
							Double value = bplustree_object.search(Integer.parseInt(get_tokens[1]));
							search_result = (value == null) ? "Null" : Double.toString(value);
						}
						// Write search result to file
						if (line_first) 
						{
							generate_output.write(search_result);
							line_first = false;
						} 
						else 
						{
							generate_output.write("\n" + search_result);
						}
						generate_output.flush();
						break;
					default:
						throw new IllegalArgumentException("\"" + get_tokens[0] + "\"" + " is an unacceptable input.");
				}
			}
			// Close file pointer
			generate_output.close();
		} 
		catch (IllegalArgumentException e) 
		{
			System.err.println(e);
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println(e);
		}
		catch (IOException e) 
		{
			System.err.println(e);
		}
		 
		
	}
}
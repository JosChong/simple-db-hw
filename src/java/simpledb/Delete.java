package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    
    private TransactionId tid;
    private OpIterator child_iterator;
    private TupleDesc td;
    
    private boolean deleted;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, OpIterator child) {
        // some code goes here
    		tid = t;
        child_iterator = child;

        Type[] type_array = new Type[] { Type.INT_TYPE };
        String[] string_array = new String[] { "Number Deleted" };
        td = new TupleDesc(type_array, string_array);
        
        deleted = false;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    		child_iterator.open();
    		super.open();
    }

    public void close() {
        // some code goes here
    		super.close();
    		child_iterator.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    		child_iterator.rewind();
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
	    	if (deleted) return null;
	    	
	    	int num_deleted = 0;
	    	while (child_iterator.hasNext())
	    	{
	    		try {
	        		Database.getBufferPool().deleteTuple(tid, child_iterator.next());    			
	    		} catch (IOException e) {
	    			System.out.println(e.getMessage());
	    		}
	    		num_deleted++;
	    	}
	    	
	    	deleted = true;
	    	Tuple tuple = new Tuple(td);
	    	tuple.setField(0, new IntField(num_deleted));
	    	return tuple;
    }

    @Override
    public OpIterator[] getChildren() {
        // some code goes here
    		return new OpIterator[] { child_iterator };
    }

    @Override
    public void setChildren(OpIterator[] children) {
        // some code goes here
    		child_iterator = children[0];
    }

}

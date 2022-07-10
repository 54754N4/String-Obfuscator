package com.satsana.so.engine.model;

import java.util.ArrayList;
import java.util.function.Function;

import com.satsana.so.engine.transforms.Permutation;
import com.satsana.so.engine.transforms.model.Transformation;

public class TransformationChain extends ArrayList<Transformation> implements Function<Long, Long> {
	private static final long serialVersionUID = 6587027146192465729L;

	@Override
	public Long apply(Long t) {
		long c = t;
		for (int i=0; i<size(); i++)
			c = get(i).transform(c);
		return c;
	}
	
	public TransformationChain reverse() {
		TransformationChain reverse = new TransformationChain();
		for (int i=size() - 1; i >= 0; --i)
			reverse.add(get(i).reversed());
		return reverse;
	}
	
	public <T extends Transformation> boolean contains(Class<T> cls) {
		for (Transformation t : this)
			if (cls.isInstance(t))
				return true;
		return false;
	}
	
	public boolean containsPermutation() {
		return contains(Permutation.class);
	}
}

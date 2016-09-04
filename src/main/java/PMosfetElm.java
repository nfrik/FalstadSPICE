
    class PMosfetElm extends MosfetElm {
	public PMosfetElm(int xx, int yy) { super(xx, yy, true); }
	@Override
	Class getDumpClass() { return MosfetElm.class; }
    }

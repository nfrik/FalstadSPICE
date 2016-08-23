

    class ACRailElm extends RailElm {
	public ACRailElm(int xx, int yy) { super(xx, yy, WF_AC); }
	@Override
	Class getDumpClass() { return RailElm.class; }
    }

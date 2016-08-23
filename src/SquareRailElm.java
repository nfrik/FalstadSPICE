
    class SquareRailElm extends RailElm {
	public SquareRailElm(int xx, int yy) { super(xx, yy, WF_SQUARE); }
	@Override
	Class getDumpClass() { return RailElm.class; }
    }

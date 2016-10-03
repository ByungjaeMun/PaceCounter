package example.android.com.pacecounter;

public class TextItem {
	/**
	 * Icon
	 */
	// private Drawable mIcon;

	/**
	 * Data array
	 */
	private String[] mData;
	private boolean mDiff;

	/**
	 * True if this item is selectable
	 */
	private boolean mSelectable = true;

	/**
	 * Initialize with icon and data array
	 * 
	 * @param icon
	 * @param obj
	 */
	public TextItem(String[] obj) {
		// mIcon = icon;
		mData = obj;
	}

	/**
	 * Initialize with icon and strings
	 * 
	 * @param icon
	 * @param obj01
	 * @param obj02
	 * @param obj03
	 */
	public TextItem(String obj01, String obj02) {
		// mIcon = icon;

		mData = new String[2];
		mData[0] = obj01;
		mData[1] = obj02;
		// mData[2] = obj03;
	}

	public TextItem(String obj01, String obj02, String obj03) {

		mData = new String[3];
		mData[0] = obj01;
		mData[1] = obj02;
		mData[2] = obj03;

	}



	/**
	 * True if this item is selectable
	 */
	public boolean isSelectable() {
		return mSelectable;
	}

	/**
	 * Set selectable flag
	 */
	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}

	/**
	 * Get data array
	 * 
	 * @return
	 */
	public String[] getData() {
		return mData;
	}

	public void setDiff(boolean mDiff) {
		this.mDiff = mDiff;
	}

	public boolean getDiff() {
		return mDiff;
	}

	/**
	 * Get data
	 */
	public String getData(int index) {
		if (mData == null || index >= mData.length) {
			return null;
		}

		return (String) mData[index];
	}

	/**
	 * Set data array
	 * 
	 * @param obj
	 */
	public void setData(String[] obj) {
		mData = obj;
	}

	/**
	 * Set icon
	 * 
	 * @param icon
	 */
	// public void setIcon(Drawable icon) {
	// mIcon = icon;
	// }

	/**
	 * Get icon
	 * 
	 * @return
	 */
	// public Drawable getIcon() {
	// return mIcon;
	// }

	/**
	 * Compare with the input object
	 * 
	 * @param other
	 * @return
	 */
	public int compareTo(TextItem other) {
		if (mData != null) {
			String[] otherData = other.getData();
			if (mData.length == otherData.length) {
				for (int i = 0; i < mData.length; i++) {
					if (!mData[i].equals(otherData[i])) {
						return -1;
					}
				}
			} else {
				return -1;
			}
		} else {
			throw new IllegalArgumentException();
		}

		return 0;
	}
}

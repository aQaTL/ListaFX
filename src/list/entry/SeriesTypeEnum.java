package list.entry;

/**
 * Created by Maciej on 21.07.2016.
 */
public enum SeriesTypeEnum
{
	TV(1), OVA(2), MOVIE(3), SPECIAL(4), ONA(5);

	private int n;

	public static SeriesTypeEnum valueOf(int number)
	{
		switch (number)
		{
			case 1:
				return TV;
			case 2:
				return OVA;
			case 3:
				return MOVIE;
			case 4:
				return SPECIAL;
			case 5:
				return ONA;

			default:
				return null;
		}
	}

	public int getNumber()
	{
		return n;
	}

	@Override
	public String toString()
	{
		return super.toString().substring(0, 1) + super.toString().substring(1).toLowerCase();
	}

	SeriesTypeEnum(int n)
	{
		this.n = n;
	}
}

package list.entry;

/**
 * Created by Maciej on 2016-05-31.
 */
public enum MyStatusEnum
{
	WATCHING(1), COMPLETED(2), ONHOLD(3), DROPPED(4), PLANTOWATCH(6);

	private int statusNumber;

	public int getStatusNumber()
	{
		return statusNumber;
	}

	public static MyStatusEnum getMyStatusEnum(String statusNumber)
	{
		switch (statusNumber)
		{
			case "1":
				return WATCHING;

			case "2":
				return COMPLETED;

			case "3":
				return ONHOLD;

			case "4":
				return DROPPED;

			case "6":
				return PLANTOWATCH;

			default:
				return null;
		}
	}

	@Override
	public String toString()
	{
		return super.toString().substring(0, 1) + super.toString().substring(1).toLowerCase();
	}

	MyStatusEnum(int statusNumber)
	{
		this.statusNumber = statusNumber;
	}
}

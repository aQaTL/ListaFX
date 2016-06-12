package list;

/**
 * Created by Maciej on 2016-06-11.
 */
public enum MyScoreEnum
{
	APPALLING(1), HORRIBLE(2), VERY_BAD(3), BAD(4), AVERAGE(5),
	FINE(6), GOOD(7), VERY_GOOD(8), GREAT(9), MASTERPIECE(10);

	int score;
	String description;

	public static MyScoreEnum getMyScoreEnum(String score)
	{
		switch (score)
		{
			case "1":
				return APPALLING;

			case "2":
				return HORRIBLE;

			case "3":
				return VERY_BAD;

			case "4":
				return BAD;

			case "5":
				return AVERAGE;

			case "6":
				return FINE;

			case "7":
				return GOOD;

			case "8":
				return VERY_GOOD;

			case "9":
				return GREAT;

			case "10":
				return MASTERPIECE;

			default:
				return null;
		}
	}

	public int getScore()
	{
		return score;
	}

	@Override
	public String toString()
	{
		return "(" + score + ") " + super.toString().substring(0, 1) + super.toString().substring(1).toLowerCase();
	}

	MyScoreEnum(int score)
	{
		this.score = score;
	}
}

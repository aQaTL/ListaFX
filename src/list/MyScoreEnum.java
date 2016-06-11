package list;

/**
 * Created by Maciej on 2016-06-11.
 */
public enum MyScoreEnum
{
	ONE(1, "Appalling"), TWO(2, "Horrible"), THREE(3, "Very bad"), FOUR(4, "Bad"), FIVE(5, "Average"),
	SIX(6, "Fine"), SEVEN(7, "Good"), EIGHT(8, "Very good"), NINE(9, "Great"), TEN(10, "Masterpiece");

	int score;
	String description;

	public static MyScoreEnum getMyScoreEnum(String score)
	{
		switch (score)
		{
			case "1":
				return ONE;

			case "2":
				return TWO;

			case "3":
				return THREE;

			case "4":
				return FOUR;

			case "5":
				return FIVE;

			case "6":
				return SIX;

			case "7":
				return SEVEN;

			case "8":
				return EIGHT;

			case "9":
				return NINE;

			case "10":
				return TEN;

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
		return "(" + score + ") " + description;
	}

	MyScoreEnum(int score, String description)
	{
		this.score = score;
		this.description = description;
	}
}

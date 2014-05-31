int sqrt(int n)
{
    int y;
    y = 0;
    int x;
    x = 0;

    while(y <= n)
    {
        y = y + 2 * x + 1;
        x = x + 1;
    }

    x = x - 1;

    return x;
}

void main()
{
    int i;
    i = 0;
    while (i <= 16)
    {
        print(sqrt(i));
        i = i + 1;
    }
}

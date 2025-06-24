# The Java String Immutability Plot Twist That'll Make You Question Your Own Sanity

Picture this: You're sitting in an interview, feeling all confident and stuff. The interviewer leans back and hits you
with the classic "So, tell me about Java String immutability." You're thinking "Pfft, easy money!" and you start
rattling off how String is totally immutable, just like you learned in your bootcamp or that dusty computer science
textbook.

Well, I am gonna bust your bubble. You may call me heretic and blasphemous, but it is what it is!

## The Answer That'll Make Your Interviewer Do a Double-Take

Here's the tea:

> Java String is technically mutable, but effectively immutable.

*Excuse me what?*

## Wait, What?! String is Mutable?!

Before you start having an existential crisis and questioning your entire Java journey, let me explain what's really
going on under the hood (don't panic).

When you crack open the String class like it's a mystery novel, you'll find two sneaky little fields that aren't marked
as `final`:

* `hash`
* `hashIsZero`.

Look here:

```java
public final class String implements
        java.io.Serializable, Comparable<String>,
        CharSequence, Constable, ConstantDesc {

    private final byte[] value; // Actual holder of String value
    private final byte coder; // either LATIN (0) or UTF16 (1)

    private int hash; // This little rebel
    private boolean hashIsZero; // And its sidekick

    // rest of the code not shown
}
```

For JDK 8 and below, the implementation is "simpler":

```java
public final class String {

    private final char value[];

    private int hash; // Still rebellious

    // rest of the code not shown
}
```

These non-final fields are basically the reason String can't sit at the "truly immutable" table with the cool kids like
`Optional` and `LocalDateTime`. Those classes have ALL their fields locked down tighter than your parent's Wi-Fi
password.

## So Why Did Java Do This to Us?

Now you're probably thinking "Why would Java betray us like this? I trusted you, Java!"

Calculating a String's hash code can be more expensive than your newly bought gift you are going to give to your crush.

Think about it - a String could be as short as how your crush curtly responded to your messages ("k"), or as long as
your sorry love story with your crush ***sob***, totally unrequited and not reciprocated.

Every time you calculate that hash code, Java has to loop through every single character like it's counting sheep. For
massive strings, this is about as fun as watching paint dry, but slower.

## Java Cares About Performance

Here's where Java gets all smart and stuff: Instead of calculating everything on-demand, Java says "You know what? I'll
calculate this hash code when someone actually needs it, and then I'll remember it forever." At least until JVM is shut
down, obviously.

Java isn't here to hold your hand and explain why your crush left you on read. Java's got bigger fish to fry - like
keeping your applications from throwing performance tantrums that could cost you your job. Hash codes aren't used that
often anyway (mainly just when you're dealing with HashMap and its gang).

So Java takes the "work smarter, not harder" approach: calculate once, cache forever, and keep your career prospects
intact. ~~Your heart might be broken, but your career would not, thank you Java.~~

If you are curious, this is how hash code is calculated in String class:

```java
// Implementation in JDK 21
public final class String implements
        java.io.Serializable, Comparable<String>,
        CharSequence, Constable, ConstantDesc {

    //
    // Other parts not shown
    //

    private int hash;
    private boolean hashIsZero;

    //
    // Other parts not shown
    //

    public int hashCode() {
        int h = this.hash;
        
        if (h == 0 && !this.hashIsZero) {
            h = isLatin1()
                    ? StringLatin1.hashCode(value)
                    : StringUTF16.hashCode(value);
            
            if (h == 0) {
                this.hashIsZero = true;
            } else {
                this.hash = h;
            }
        }
        
        return h;
    }

    //
    // Other parts not shown
    //
}
```

## We Came to the "Effectively" Immutable Part!

Here's the beautiful part - all this internal drama with hash code caching? It's completely invisible from the outside!
It's like a really good magic trick where you know something's happening behind the curtain, but you can't see it.

Unless you're one of those people who likes to play with fire and use Reflection API (why though?) or go completely off
the rails with something like Cheat Engine (seriously, you go full heresy now?), you'll never notice these internal
shenanigans.

The hash code gets calculated, cached, and from that point on, it's stable as how your crush only sees you as a backup
plan, best friend forever (don't even try to deny that!).

So, indeed, String is technically mutable because of these internal changes, but practically speaking, it behaves like
it's completely immutable, and reaps all the benefits from being one (JVM optimization notwithstanding). It works just
as good as any other immutable classes you know and love.

## The Real Talk

This whole situation is actually a masterclass in pragmatic programming. They chose the path that gives you all the
benefits of immutability that you actually care about:

- Thread safety without the headache of synchronization
- You can pass Strings around like they're going out of style without worrying about side effects
- HashMaps won't have nervous breakdowns
- No need for defensive copying (ain't nobody got time for dat)

So next time someone asks you about Java String immutability in an interview, you can drop this knowledge bomb and watch
their face go through the five stages of grief.

"Well," you can say with a knowing smile:

> "Java String is technically mutable due to lazy hash code initialization, but effectively immutable for all practical
> purposes".

Yes, there is no beating around the bush here. Just straight up cold and hard facts. Some compromises have to be made
~~and sacrificed to the Programming Gods~~ so that your program can run smoothly 99.99999999% most of the time.

## Bonus: The Future is Looking Bright (And Actually Immutable?)

(At the time of writing: 2025-06-24)

But wait, there's more! Just when you thought this story was over, Java 25 is coming in September 2025 like a superhero
sequel nobody asked for but everyone's secretly excited about.

The OpenJDK folks are cooking up some spicy enhancements, and they're about to make our "technically mutable" String
situation even more interesting. In Java 25, they're introducing some JVM magic that will treat the hash field as "
stable" - basically telling the JVM "Hey, trust us, this field is never gonna change after it's set, so go ahead and
optimize the heck out of it."

It's like finally getting your parents to trust you with the car keys, except the car is String performance and the keys
are advanced JVM optimizations.

You can check out the juicy details [here](https://inside.java/2025/05/01/strings-just-got-faster) if you're into that
sort of technical gossip.

But here's where it gets really spicy - there are already JEP drafts floating around (JEP 502 and its squad) that will
introduce something called "stable values." Think of these as fields that act like true final fields once they're
initialized. It's like commitment-phobic fields finally deciding to settle down after their wild initialization phase.

This is actually a bigger deal than it sounds, because right now, String can't be made into a value class by the
Valhalla project (Java's attempt to make "code like an Integer, work like an int"). The current "technically mutable"
status is like that one friend who can't commit to group vacation plans - it's holding everyone back.

But who knows what the good folks at OpenJDK are secretly plotting for String? Maybe in a few years, we'll have truly
immutable Strings that are also value classes, running faster than your excuses when you're late for a meeting.
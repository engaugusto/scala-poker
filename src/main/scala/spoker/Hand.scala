package spoker

package object hand {

  import spoker.cards._
  import spoker.cards.Rank._

  object Hand {
    def apply(seq: Cards): Hand = {
      implicit val cards = seq.sorted
      cards match {
        case Broadway(Straight(Flush(_))) => new RoyalFlush
        case Straight(Flush(_)) => new StraightFlush
        case FourOfAKind(_) => new FourOfAKind
        case ThreeOfAKind(OnePair(_)) => new FullHouse
        case Flush(_) => new Flush
        case Straight(_) => new Straight
        case ThreeOfAKind(_) => new ThreeOfAKind
        case TwoPairs(_) => new TwoPair
        case OnePair(_) => new OnePair
        case (_) => new HighCard
      }
    }
  }

  object Broadway {
    def unapply(cards: Cards) =
      cards map {
        _.rank
      } last match {
        case Ace => Some(cards)
        case _ => None
      }
  }

  object Straight {
    def unapply(cards: Cards) = {
      val first = cards.head.rank.id
      val S = first to first + 4
      cards map (_.rank.id) match {
        case S => Some(cards)
        case _ => None
      }
    }
  }

  object Flush {
    def unapply(cards: Cards) =
      cards map {
        _.suit
      } forall {
        _ == cards.head.suit
      } match {
        case true => Some(cards)
        case _ => None
      }
  }

  object OnePair extends PairAwareness {
    def unapply(cards: Cards) = numberOfPairs(cards) match {
      case 1 => Some(cards)
      case _ => None
    }
  }

  object TwoPairs extends PairAwareness {
    def unapply(cards: Cards) = numberOfPairs(cards) match {
      case 2 => Some(cards)
      case _ => None
    }
  }

  object ThreeOfAKind extends PairAwareness {
    def unapply(cards: Cards) = numberOfGroupsOf(3)(cards) match {
      case 1 => Some(cards)
      case _ => None
    }
  }

  object FourOfAKind extends PairAwareness {
    def unapply(cards: Cards) = numberOfGroupsOf(4)(cards) match {
      case 1 => Some(cards)
      case _ => None
    }
  }

  trait PairAwareness extends GroupAwareness {
    val numberOfPairs = numberOfGroupsOf(2)_

    def onePair(implicit cards: Cards) = 1 == numberOfPairs(cards)

    def twoPairs(implicit cards: Cards) = 2 == numberOfPairs(cards)
  }

  trait GroupAwareness {
    protected def numberOfGroupsOf(x: Int)(cards: Cards) =
      cards map {
        _.rank
      } groupBy (identity) filter {
        x == _._2.size
      } size
  }

  sealed abstract class Hand(cards: Cards) extends Ordered[Hand] {
    def compare(that: Hand): Int = this.ranking.compareTo(that.ranking)

    val ranking = HandRanking withName this.getClass.getSimpleName
  }

  object HandRanking extends Enumeration {
    val HighCard, OnePair, TwoPair, ThreeOfAKind, Straight, Flush, FullHouse, FourOfAKind, StraightFlush, RoyalFlush =
      Value
  }

  class RoyalFlush(implicit cards: Cards) extends Hand(cards)

  class StraightFlush(implicit cards: Cards) extends Hand(cards)

  class FourOfAKind(implicit cards: Cards) extends Hand(cards)

  class FullHouse(implicit cards: Cards) extends Hand(cards)

  class Flush(implicit cards: Cards) extends Hand(cards)

  class Straight(implicit cards: Cards) extends Hand(cards)

  class ThreeOfAKind(implicit cards: Cards) extends Hand(cards)

  class TwoPair(implicit cards: Cards) extends Hand(cards)

  class OnePair(implicit cards: Cards) extends Hand(cards)

  class HighCard(implicit cards: Cards) extends Hand(cards)
}
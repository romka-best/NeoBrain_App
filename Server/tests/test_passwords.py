import pytest
from data.users import correct_password


def test_correct_password_wrong():
    assert correct_password("1234567") == ["Less than 8 characters", "Uppercase only or lowercase only"]
    assert correct_password("abc") == ["Less than 8 characters", "Uppercase only or lowercase only", "Missing digits"]
    assert correct_password("aBc1234") == ["Less than 8 characters"]
    assert correct_password("abc123abc") == ["Uppercase only or lowercase only"]
    assert correct_password("ABC123ABC") == ["Uppercase only or lowercase only"]
    assert correct_password("ABCDEEDCBA") == ["Uppercase only or lowercase only", "Missing digits"]
    assert correct_password("abcabcABC") == ["Missing digits"]
    assert correct_password("ABCabcABC") == ["Missing digits"]
    assert correct_password("False_True") == ["Missing digits"]
    assert correct_password("AbcDef") == ["Less than 8 characters", "Missing digits"]


def test_correct_password_correct():
    assert correct_password("Aa1234bB") == ("OK", 8)
    assert correct_password("123AbC78") == ("OK", 8)
    assert correct_password("123AbC789JqP") == ("OK", 10)
    assert correct_password("False_True123{l") == ("OK", 12)

Create FUNCTION FN9_CHAR (expr1 varchar(10))
 RETURNS varchar(10)
 BEGIN
   DECLARE s1 varchar(10);
   SET s1 = char(expr1);
   RETURN(s1);
END;
;



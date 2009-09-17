Create FUNCTION FN9_CONCAT
  (expr1 varchar(1600), expr2 varchar(1600), expr3 varchar(1600))
 RETURNS varchar(5000)
 BEGIN
   DECLARE c varchar(5000);
      SET c = concat(expr1,expr2,expr3);
      RETURN(c);
END


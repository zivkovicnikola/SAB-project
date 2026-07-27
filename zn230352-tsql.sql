USE [Movies_SAB]
GO
/****** Object:  Trigger [dbo].[TR_BLOCK_EXTREME]    Script Date: 6/14/2026 3:58:51 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TR_BLOCK_EXTREME]
ON [dbo].[Grade]
FOR INSERT, UPDATE
AS
BEGIN
    DECLARE @IdGrade INT;
    DECLARE @IdU INT;
    DECLARE @IdM INT;

    DECLARE @IdG INT;
    DECLARE @ExtremeCount INT;
    DECLARE @NeutralCount INT;

    DECLARE gradeCursor CURSOR FOR
        SELECT Id, IdU, IdM
        FROM inserted
        WHERE Grade IN (1, 10);

    OPEN gradeCursor;

    FETCH NEXT FROM gradeCursor
    INTO @IdGrade, @IdU, @IdM;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        DECLARE genreCursor CURSOR FOR
            SELECT IdG
            FROM MovieGenre
            WHERE IdM = @IdM;

        OPEN genreCursor;

        FETCH NEXT FROM genreCursor
        INTO @IdG;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            SELECT @ExtremeCount = COUNT(*)
            FROM Grade G INNER JOIN MovieGenre MG ON MG.IdM = G.IdM
            WHERE G.IdU = @IdU AND MG.IdG = @IdG AND G.Grade IN (1, 10) AND G.Id != @IdGrade;

            SELECT @NeutralCount = COUNT(*)
            FROM Grade G INNER JOIN MovieGenre MG ON MG.IdM = G.IdM
            WHERE G.IdU = @IdU AND MG.IdG = @IdG AND G.Grade IN (6, 7, 8);

            IF @ExtremeCount > 3 AND @NeutralCount < 3
            BEGIN
                CLOSE genreCursor;
                DEALLOCATE genreCursor;

                CLOSE gradeCursor;
                DEALLOCATE gradeCursor;

                RAISERROR('Korisnik ne moze da da ekstremnu ocenu u ovom zanru.', 16, 1);
                ROLLBACK TRANSACTION;
                RETURN;
            END

            FETCH NEXT FROM genreCursor
            INTO @IdG;
        END

        CLOSE genreCursor;
        DEALLOCATE genreCursor;

        FETCH NEXT FROM gradeCursor
        INTO @IdGrade, @IdU, @IdM;
    END

    CLOSE gradeCursor;
    DEALLOCATE gradeCursor;
END

USE [Movies_SAB]
GO
/****** Object:  Trigger [dbo].[TR_UPDATE_MOVIE_TREND]    Script Date: 6/14/2026 3:50:56 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TR_UPDATE_MOVIE_TREND]
ON [dbo].[Grade]
AFTER INSERT, UPDATE, DELETE
AS
BEGIN

    DECLARE @IdM INT;
    DECLARE @CountLast30 INT;
    DECLARE @AvgTotal DECIMAL(10,3);
    DECLARE @MaxLast30 INT;
    DECLARE @AvgLast5 DECIMAL(10,3);
    DECLARE @TotalCount INT;

    DECLARE @NewStatus NVARCHAR(100);

    DECLARE movieCursor CURSOR FOR
        SELECT DISTINCT IdM
        FROM (
            SELECT IdM FROM inserted
            UNION
            SELECT IdM FROM deleted
        ) AS ChangedMovies;

    OPEN movieCursor;
    FETCH NEXT FROM movieCursor INTO @IdM;

    WHILE @@FETCH_STATUS = 0
    BEGIN

        SELECT @CountLast30 = COUNT(*)
        FROM Grade
        WHERE IdM = @IdM AND CreatedAt >= DATEADD(DAY, -30, GETDATE());

        SELECT @MaxLast30 = MAX(Cnt)
        FROM (
            SELECT IdM, COUNT(*) AS Cnt
            FROM Grade
            WHERE CreatedAt >= DATEADD(DAY, -30, GETDATE())
            GROUP BY IdM
        ) AS Counts;

        SELECT @AvgLast5 = AVG(CAST(Grade AS DECIMAL(10,3)))
        FROM (
            SELECT TOP 5 Grade
            FROM Grade
            WHERE IdM = @IdM
            ORDER BY CreatedAt DESC, Id DESC
        ) AS Last5;

        SELECT @AvgTotal = AVG(CAST(Grade AS DECIMAL(10,3))), @TotalCount = COUNT(*)
        FROM Grade
        WHERE IdM = @IdM;

        IF @TotalCount >= 5 AND @AvgLast5 >= @AvgTotal + 1
            SET @NewStatus = 'Rising';
        ELSE IF @TotalCount >= 5 AND @AvgLast5 <= @AvgTotal - 1
            SET @NewStatus = 'Falling';
        ELSE IF @TotalCount >= 3 AND @AvgTotal >= 8
            SET @NewStatus = 'Classic';
        ELSE IF @CountLast30 > 0 AND @CountLast30 = @MaxLast30
            SET @NewStatus = 'Trending';
        ELSE
            SET @NewStatus = NULL;

        UPDATE Movie
        SET Status = @NewStatus
        WHERE Id = @IdM;

        FETCH NEXT FROM movieCursor INTO @IdM;
    END

    CLOSE movieCursor;
    DEALLOCATE movieCursor;
END

USE [Movies_SAB]
GO

/****** Object:  StoredProcedure [dbo].[SP_REWARD_USER_]    Script Date: 6/14/2026 4:25:55 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SP_REWARD_USER_] 
	@idU INT,
	@idM INT
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @CntGraded INT;
	DECLARE @MostLoved INT;
	DECLARE @AvgTotal DECIMAL(10,3);
	DECLARE @UserGrade INT;

	SELECT @CntGraded = COUNT(*)
	FROM Grade
	WHERE IdU = @idU;

	IF @CntGraded < 10
		RETURN;

	SELECT @UserGrade = Grade
    FROM Grade
    WHERE IdU = @IdU AND IdM = @IdM;

    IF @UserGrade IS NULL
        RETURN;

	SELECT @MostLoved = COUNT(*)
	FROM MovieGenre MG
	WHERE MG.IdM = @IdM
	  AND MG.IdG IN (
			SELECT MG2.IdG
			FROM MovieGenre MG2 JOIN Grade G ON G.IdM = MG2.IdM
			WHERE G.IdU = @IdU
			GROUP BY MG2.IdG
			HAVING AVG(CAST(G.Grade AS DECIMAL(10,3))) >= 8
	  );

	IF @MostLoved = 0
		RETURN;

    SELECT @AvgTotal = AVG(CAST(Grade AS DECIMAL(10,3)))
    FROM Grade
    WHERE IdM = @IdM AND IdU != @idU;

	IF @AvgTotal IS NULL
		SET @AvgTotal = 0;

	IF @AvgTotal < 6
		BEGIN
			UPDATE Users
			SET Awards = Awards + 1
			WHERE Id = @idU
		END

END
GO

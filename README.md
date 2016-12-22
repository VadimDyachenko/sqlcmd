# sqlcmd
Учебный проект
Для подключения к базе данных необходимо указать параметры подключения в файле /configuration/sqlcmd.properties

Программа поддерживает два языка: английский и русский.
Для выбора предпочтительного языка необходимо изменить параметр sqlcmd.language на en или ru.
При выборе не поддерживаемого языка будет установлен язык по умолчанию - en

Навигация по меню осуществляется посредством выбора номера команды. На экран будут выведены все доступные команды в виде меню.
Меню двухуровневое. Для работы с базой данных и для работы с таблицей.

В любой момент времени можно ввести команду exit, чтобы немедленно прекратить выполнение программы. 
При этом будут закрыты соединения с БД и завершена работа.

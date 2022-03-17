--
-- PostgreSQL database dump
--

-- Dumped from database version 12.9 (Ubuntu 12.9-0ubuntu0.20.04.1)
-- Dumped by pg_dump version 12.9 (Ubuntu 12.9-0ubuntu0.20.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: games; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.games (
    hometeamname text NOT NULL,
    awayteamname text NOT NULL,
    hometeamscore numeric,
    awayteamscore numeric,
    date timestamp without time zone NOT NULL,
    quarter integer,
    hometeamabrv text,
    awayteamabrv text,
    hometeamwins integer,
    hometeamlosses integer,
    awayteamwins integer,
    awayteamlosses integer
);


ALTER TABLE public.games OWNER TO postgres;

--
-- Name: hasteamsubscription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.hasteamsubscription (
    email text NOT NULL,
    league text,
    teamname text NOT NULL
);


ALTER TABLE public.hasteamsubscription OWNER TO postgres;

--
-- Name: teams; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.teams (
    teamname text NOT NULL,
    teamabrv text,
    record text,
    nextopponent text,
    league text,
    city text,
    wins integer,
    losses integer
);


ALTER TABLE public.teams OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    email text NOT NULL,
    firstname text,
    lastname text,
    username text,
    birthday timestamp without time zone
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Data for Name: games; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.games (hometeamname, awayteamname, hometeamscore, awayteamscore, date, quarter, hometeamabrv, awayteamabrv, hometeamwins, hometeamlosses, awayteamwins, awayteamlosses) FROM stdin;
Nuggets	Pelicans	138	130	2022-03-06 00:00:00	\N	DEN	NOP	39	26	25	35
Kings	Knicks	115	131	2022-03-07 00:00:00	\N	SAC	NYK	24	43	27	38
Nuggets	Warriors	131	124	2022-03-07 00:00:00	\N	DEN	GSW	39	26	43	22
Spurs	Lakers	117	110	2022-03-07 00:00:00	\N	SAS	LAL	25	40	28	36
Mavericks	Jazz	111	103	2022-03-07 00:00:00	\N	DAL	UTA	40	25	40	24
Timberwolves	Trail Blazers	124	81	2022-03-07 00:00:00	\N	MIN	POR	37	29	25	39
Heat	Rockets	123	106	2022-03-07 00:00:00	\N	MIA	HOU	44	22	16	49
76ers	Bulls	121	106	2022-03-07 00:00:00	\N	PHI	CHI	40	24	39	26
Pistons	Hawks	113	110	2022-03-07 00:00:00	\N	DET	ATL	18	47	31	33
Clippers	Knicks	93	116	2022-03-06 00:00:00	\N	LAC	NYK	34	32	27	38
Cavaliers	Raptors	104	96	2022-03-06 00:00:00	\N	CLE	TOR	37	27	34	30
Thunder	Jazz	103	116	2022-03-06 00:00:00	\N	OKC	UTA	20	44	40	24
Rockets	Grizzlies	123	112	2022-03-06 00:00:00	\N	HOU	MEM	16	49	44	22
Wizards	Pacers	133	123	2022-03-06 00:00:00	\N	WAS	IND	29	34	22	44
Bucks	Suns	132	122	2022-03-06 00:00:00	\N	MIL	PHX	40	25	51	13
Celtics	Nets	126	120	2022-03-06 00:00:00	\N	BOS	BKN	39	27	32	33
Lakers	Warriors	124	116	2022-03-05 00:00:00	\N	LAL	GSW	28	36	43	22
Heat	76ers	99	82	2022-03-05 00:00:00	\N	MIA	PHI	44	22	40	24
Grizzlies	Magic	124	96	2022-03-05 00:00:00	\N	MEM	ORL	44	22	16	49
Timberwolves	Trail Blazers	135	121	2022-03-05 00:00:00	\N	MIN	POR	37	29	25	39
Hornets	Spurs	123	117	2022-03-05 00:00:00	\N	CHA	SAS	32	33	25	40
Mavericks	Kings	114	113	2022-03-05 00:00:00	\N	DAL	SAC	40	25	24	43
\.


--
-- Data for Name: hasteamsubscription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.hasteamsubscription (email, league, teamname) FROM stdin;
gcbourdon@gmail.com	\N	Celtics
gcbourdon@gmail.com	\N	Kings
gcbourdon@gmail.com	\N	Suns
test16@gmail.com	\N	Clippers
test16@gmail.com	\N	Cavaliers
test16@gmail.com	\N	Celtics
\.


--
-- Data for Name: teams; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.teams (teamname, teamabrv, record, nextopponent, league, city, wins, losses) FROM stdin;
Pelicans	NOP	27-37	Grizzlies	NBA	New Orleans	\N	\N
Heat	MIA	44-22	Suns	NBA	Miami	44	22
76ers	PHI	40-24	Nets	NBA	Philadelphia	40	24
Bucks	MIL	40-25	Hawks	NBA	Milwaukee	40	25
Bulls	CHI	39-26	Pistons	NBA	Chicago	39	26
Celtics	BOS	39-27	Hornets	NBA	Boston	39	27
Cavaliers	CLE	37-27	Pacers	NBA	Clevland	37	27
Raptors	TOR	34-30	Spurs	NBA	Toronto	34	30
Hornets	CHA	32-33	Celtics	NBA	Charlotte	32	33
Nets	BKN	32-33	Hornets	NBA	Brooklyn	32	33
Hawks	ATL	31-33	Bucks	NBA	Atlanta	31	33
Wizards	WAS	29-34	Clippers	NBA	Washington	29	34
Knicks	NYK	27-38	Mavericks	NBA	New York	27	38
Pacers	IND	22-44	Cavaliers	NBA	Indiana	22	44
Pistons	DET	18-47	Bulls	NBA	Detroit	18	47
Magic	ORL	16-49	Suns	NBA	Orlando	16	49
Suns	PHX	51-13	Magic	NBA	Phoenix	51	13
Grizzlies	MEM	44-22	Pelicans	NBA	Memphis	44	22
Warriors	GSW	42-22	Clippers	NBA	Golden State	43	22
Jazz	UTA	40-24	Trail Blazers	NBA	Utah	40	24
Mavericks	DAL	40-25	Knicks	NBA	Dallas	40	25
Nuggets	DEN	39-26	Kings	NBA	Denver	39	26
Timberwolves	MIN	37-29	Thunder	NBA	Minnesota	37	29
Clippers	LAC	34-32	Warriors	NBA	Los Angeles	34	32
Lakers	LAL	28-36	Rockets	NBA	Los Angeles	28	36
Trail Blazers	POR	25-39	Jazz	NBA	Portland	25	39
Spurs	SAS	25-40	Raptors	NBA	San Antonio	25	40
Kings	SAC	24-43	Nuggets	NBA	Sacramento	24	43
Thunder	OKC	20-44	Bucks	NBA	Oklahoma City	20	44
Rockets	HOU	16-49	Lakers	NBA	Houston	16	49
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (email, firstname, lastname, username, birthday) FROM stdin;
gcbourdon@gmail.com	Griffin	Bourdon	gcbourdon	2000-05-11 00:00:00
msyost@asu.edu	\N	\N	null	\N
jdoe@gmail.com	Jon	Doe	JonDoe	1993-03-09 00:00:00
test8@gmail.com	matthew	yost	test8	2005-03-02 00:00:00
test9@gmail.com	Matthew	Yost	test9	2022-03-18 00:00:00
test10@gmail.com	Matt	Yost	test10	2022-03-18 00:00:00
test11@gmail.com	matt	yost	test11	2022-03-18 00:00:00
test14@gmail.com	bob	bob	test14	2022-03-18 00:00:00
test15@gmail.com	bob	bob	test15	2022-03-18 00:00:00
test16@gmail.com	mat	yost	test16	2022-03-17 00:00:00
testing@gmail.com	test	test	testing	2022-03-01 00:00:00
kevinobrien1717@gmail.com			kevinobrien	2022-03-17 00:00:00
\.


--
-- Name: games games_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.games
    ADD CONSTRAINT games_pkey PRIMARY KEY (hometeamname, awayteamname, date);


--
-- Name: hasteamsubscription hasteamsubscription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.hasteamsubscription
    ADD CONSTRAINT hasteamsubscription_pkey PRIMARY KEY (email, teamname);


--
-- Name: teams teams_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT teams_pkey PRIMARY KEY (teamname);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (email);


--
-- Name: games games_awayteamname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.games
    ADD CONSTRAINT games_awayteamname_fkey FOREIGN KEY (awayteamname) REFERENCES public.teams(teamname);


--
-- Name: games games_hometeamname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.games
    ADD CONSTRAINT games_hometeamname_fkey FOREIGN KEY (hometeamname) REFERENCES public.teams(teamname);


--
-- Name: hasteamsubscription hasteamsubscription_email_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.hasteamsubscription
    ADD CONSTRAINT hasteamsubscription_email_fkey FOREIGN KEY (email) REFERENCES public.users(email);


--
-- Name: hasteamsubscription hasteamsubscription_teamname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.hasteamsubscription
    ADD CONSTRAINT hasteamsubscription_teamname_fkey FOREIGN KEY (teamname) REFERENCES public.teams(teamname);


--
-- PostgreSQL database dump complete
--


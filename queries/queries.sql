/*
SELECT c.nummer, c.nbident, (d.flaeche_gerundet - c.flaeche_gerundet) as flaeche_diff, c.gem_bfs, c.los, c.lieferdatum FROM
(
 SELECT b.nummer, b.nbident, ST_Area(a.geometrie) as flaeche, round(ST_Area(a.geometrie)::numeric, 0) as flaeche_gerundet, a.gem_bfs, a.los, a.lieferdatum FROM
 av_dm01avch24d_lv03.liegenschaften_liegenschaft as a, 
 av_dm01avch24d_lv03.liegenschaften_grundstueck as b
 WHERE a.liegenschaft_von = b.tid
 --LIMIT 10
) as c,
(
 SELECT b.nummer, b.nbident, ST_Area(a.geometrie) as flaeche, round(ST_Area(a.geometrie)::numeric, 0) as flaeche_gerundet, a.gem_bfs, a.los, a.lieferdatum FROM
 av_dm01avch24d_lv95.liegenschaften_liegenschaft as a, 
 av_dm01avch24d_lv95.liegenschaften_grundstueck as b
 WHERE a.liegenschaft_von = b.tid
 --LIMIT 10
) as d
WHERE c.nummer = d.nummer
AND c.nbident = d.nbident
*/

 SELECT b.nummer, b.nbident, ST_Area(a.geometrie) as flaeche, round(ST_Area(a.geometrie)::numeric, 0)::integer as flaeche_gerundet, a.flaechenmass, (a.flaechenmass - round(ST_Area(a.geometrie)::numeric, 0)::integer)::integer as differenz, a.geometrie, a.gem_bfs, a.los, a.lieferdatum FROM
 av_dm01avch24d_lv03.liegenschaften_liegenschaft as a, 
 av_dm01avch24d_lv03.liegenschaften_grundstueck as b
 WHERE a.liegenschaft_von = b.tid
 LIMIT 10


SELECT b.nummer, b.nbident, ST_Area(a.geometrie) as flaeche, round(ST_Area(a.geometrie)::numeric, 0)::integer as flaeche_gerundet, a.flaechenmass,  (a.flaechenmass - round(ST_Area(a.geometrie)::numeric, 0)::integer)::integer as differenz, a.geometrie, a.gem_bfs, a.los, a.lieferdatum FROM
av_dm01avch24d_lv03.liegenschaften_liegenschaft as a, 
av_dm01avch24d_lv03.liegenschaften_grundstueck as b
WHERE a.liegenschaft_von = b.tid
AND abs((a.flaechenmass - round(ST_Area(a.geometrie)::numeric, 0)::integer)::integer) > 1
ORDER BY a.gem_bfs
limit 10
def has_word(word, lines)
  lines.each do |accepted_line|
    first_half = accepted_line.split(':').first
    if first_half.strip != '' && first_half == word
      puts 'match:'
      puts accepted_line
      puts word
      return accepted_line
    else
    end
  end
  return false
end

file = File.open(ARGV.first)
lines = []
file.each do |line|
  english = line.split(':').first
  if (matching_line = has_word(english, lines))
    if matching_line == line or english + ':' == line
      break
    else
      puts 'keep: '
      puts '  ' + matching_line
      puts 'or'
      puts '  ' + line
      response = gets.chomp
    end
  end
  lines << line unless has_word(english, lines)
end
#File.new(ARGV.first + '-nodupes', 'w').write('stuff')

package org.training360.finalexam.teams;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.training360.finalexam.NotFoundException;
import org.training360.finalexam.players.CreatePlayerCommand;
import org.training360.finalexam.players.Player;
import org.training360.finalexam.players.PlayerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeamService {

    private ModelMapper modelMapper;
    private TeamRepository teamRepository;
    private PlayerRepository playerRepository;


    public List<TeamDTO> getTeams() {
        return teamRepository.findAll().stream()
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .collect(Collectors.toList());
    }

    public TeamDTO createNewTeam(CreateTeamCommand command) {
        return modelMapper.map(teamRepository.save(new Team(
                command.getName()
        )), TeamDTO.class);
    }

    @Transactional
    public TeamDTO addNewPlayerToExistingTeam(Long id, CreatePlayerCommand command) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        Player player = new Player(
                command.getName(),
                command.getDateOfBirth(),
                command.getPosition()
        );
        team.addPlayer(player);
        return modelMapper.map(team, TeamDTO.class);
    }

    @Transactional
    public TeamDTO addExistingPlayerToExistingTeam(Long id, UpdateWithExistingPlayerCommand command) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        Player player = playerRepository.findById(command.getPlayerId())
                .orElseThrow(() -> new NotFoundException(id));
        hasFreePlace(team, player);
        if (player.hasNoTeam() && hasFreePlace(team, player)) {
            team.addPlayer(player);
        }
        return modelMapper.map(team, TeamDTO.class);
    }

    private boolean hasFreePlace(Team team, Player player) {
        return team.getPlayers().stream()
                .filter(p -> p.getPosition() == player.getPosition())
                .collect(Collectors.toList()).size() < 2;
    }
}
